package swp.koi.service.lotService;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.*;
import swp.koi.repository.*;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.auctionTypeService.AuctionTypeService;
import swp.koi.service.bidService.BidServiceImpl;
import swp.koi.service.fireBase.FCMService;
import swp.koi.service.invoiceService.InvoiceService;
import swp.koi.service.redisService.RedisServiceImpl;
import swp.koi.service.socketIoService.EventListenerFactoryImpl;
import swp.koi.service.socketIoService.SocketDetail;
import swp.koi.service.vnPayService.VnpayServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final AccountService accountService;
    private final LotRepository lotRepository;
    private final BidServiceImpl bidService;
    private final LotRegisterRepository lotRegisterRepository;
    private final KoiFishRepository koiFishRepository;
    private final AuctionRepository auctionRepository;
    private final VnpayServiceImpl vnpayService;
    private final MemberRepository memberRepository;
    private final InvoiceRepository invoiceRepository;
    private final EventListenerFactoryImpl eventListenerFactory;
    private final SocketIOServer socketServer;
    private final RedisServiceImpl redisServiceImpl;
    private final FCMService fcmService;
    private final InvoiceService invoiceService;
    private final AuctionTypeService auctionTypeService;

    @Override
    public Lot findLotById(int id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));
    }

    @Override
    @Async
    @Scheduled(fixedRate = 1000 * 20) // Run every 60 seconds
    public void startLotBy() {
        LocalDateTime now = LocalDateTime.now();

        List<Lot> waitingLots = lotRepository.findAllByStatusAndStartingTimeLessThan(LotStatusEnum.WAITING, now);
        waitingLots.forEach(this::startLot);

        List<Lot> runningLots = lotRepository.findAllByStatusAndEndingTimeLessThan(LotStatusEnum.AUCTIONING, now);
        runningLots.forEach(this::endLot);

        AuctionType auctionType = auctionTypeService.findByAuctionTypeName("DESCENDING_BID");
        List<Lot> descendingLots = lotRepository.findAllByStatusAndAuctionType(LotStatusEnum.AUCTIONING, auctionType);
        descendingLots.forEach(this::decreasePrice);

        System.out.println("--------------------------------------------Scanning--------------------------------------------");
    }



    private void startLot(Lot lot) {
        updateKoiFishStatus(lot.getKoiFish(), KoiFishStatusEnum.AUCTIONING);

        List<Lot> list = new ArrayList<>();
        list.add(lot);

        Auction auction = auctionRepository.findByLots(list);

        updateAuctionStatus(auction, AuctionStatusEnum.AUCTIONING);
        lot.setStatus(LotStatusEnum.AUCTIONING);

        lotRepository.save(lot);
    }

    @Override
    public void endLot(Lot lot) {
        List<Bid> bidList = bidService.listBidByLotId(lot.getLotId());

        if (bidList.isEmpty()) {
            setLotToPassed(lot);
        } else {
            concludeLot(lot, bidList);
        }
        //real time update
        notifyClient(lot);
        //send push notification to user who followed this lot
        sendNotificateToFollower(lot);

    }

    private void notifyClient(Lot lot) {
        SocketDetail socketDetail = SocketDetail.builder()
                .lotId(lot.getLotId())
                .newPrice(lot.getCurrentPrice())
                .build();

        eventListenerFactory.sendDataToClient(socketDetail,lot.getLotId().toString());
    }

    private void decreasePrice(Lot lot) {

        Duration timeDiff = Duration.between(lot.getStartingTime(), LocalDateTime.now());

        if (timeDiff.toMinutes() % 60 == 0) {
            lot.setCurrentPrice((float) (lot.getCurrentPrice() * 0.95));
        } else {
            System.out.println("hi");
        }

    }

    @Override
    public List<Lot> createLots(List<Lot> lots) {
        return lotRepository.saveAll(lots);
    }

    private void setLotToPassed(Lot lot) {
        updateKoiFishStatus(lot.getKoiFish(), KoiFishStatusEnum.WAITING);
        lot.setStatus(LotStatusEnum.PASSED);
        updateStatusForAllLotRegister(lot);
        lotRepository.save(lot);
    }

    private void updateStatusForAllLotRegister(Lot lot) {

        List<LotRegister> lotRegisters = lotRegisterRepository.findAllByLot(lot).orElse(null);

        if (lotRegisters != null && !lotRegisters.isEmpty()) {
            lotRegisters.stream().forEach(lotRegister -> {
                lotRegister.setStatus(LotRegisterStatusEnum.LOSE);
                lotRegisterRepository.save(lotRegister);
            });
        }

    }

    private void concludeLot(Lot lot, List<Bid> bidList) {
        Bid winningBid = chooseLotWinner(lot, bidList);
        Member winningMember = winningBid.getMember();

        updateKoiFishStatus(lot.getKoiFish(), KoiFishStatusEnum.SOLD);

        lot.setCurrentPrice(winningBid.getBidAmount());
        lot.setCurrentMemberId(winningMember.getMemberId());
        lot.setStatus(LotStatusEnum.SOLD);
        lotRepository.save(lot);

        updateLotRegisterStatus(lot, winningMember);
        markOtherBidsAsLost(lot, winningMember);

        sendNotificateToFollower(lot);
        sendNotificationToBidder(lot, winningBid);

        createInvoiceForLot(lot, winningMember);
    }

    @Async
    protected void sendNotificationToBidder(Lot lot, Bid winningBid) {
        Set<SubscribeRequest> subscribeRequests = (Set<SubscribeRequest>) redisServiceImpl.getSetData("Notify_"+lot.getLotId().toString());

        if(subscribeRequests.isEmpty()) {
            return;
        }
        subscribeRequests.stream()
                .filter(request -> !request.getMemberId().equals(winningBid.getMember().getMemberId()))
                .forEach( lr -> {
                    String msgBody = "You have lost at lot " + lot.getLotId() + " of auction " + lot.getAuction().getAuctionId();
                    fcmService.sendPushNotification("Bidding result of PrestigeKoi", msgBody, lr.getToken());
                });
    }

    private void updateKoiFishStatus(KoiFish koiFish, KoiFishStatusEnum status) {
        koiFish.setStatus(status);
        koiFishRepository.save(koiFish);
    }

    private void updateAuctionStatus(Auction auction, AuctionStatusEnum status) {
        auction.setStatus(status);
        auctionRepository.save(auction);
    }

    private void updateLotRegisterStatus(Lot lot, Member member) {
        LotRegister lotRegister = lotRegisterRepository
                .findLotRegisterByLotAndMember(lot, member);
        lotRegister.setStatus(LotRegisterStatusEnum.WON);
        lotRegisterRepository.save(lotRegister);
    }

    private void markOtherBidsAsLost(Lot lot, Member winner) {
        lotRegisterRepository.findAllByLot(lot).ifPresent(lotRegisters -> {
            lotRegisters.stream()
                    .filter(lr -> !lr.getMember().getMemberId().equals(winner.getMemberId()))
                    .forEach(lr -> {
                        lr.setStatus(LotRegisterStatusEnum.LOSE);
                        lotRegisterRepository.save(lr);
                    });
        });
    }

    private void createInvoiceForLot(Lot lot, Member winner) {
        Invoice invoice = invoiceService.createInvoiceForAuctionWinner(lot.getLotId(), winner.getMemberId());
        invoiceRepository.save(invoice);
    }

    private Bid chooseLotWinner(Lot lot, List<Bid> bidList) {

        return switch (lot.getAuctionType().getAuctionTypeName()) {
            case FIXED_PRICE_SALE -> getFixedPriceWinner(bidList);
            case SEALED_BID, ASCENDING_BID -> getHighestBid(bidList);
            case DESCENDING_BID -> getFirstBid(bidList);
        };
    }

    private Bid getFixedPriceWinner(List<Bid> bidList) {
        Collections.shuffle(bidList);
        return bidList.getFirst();
    }

    private Bid getHighestBid(List<Bid> bidList) {
        return bidList.stream()
                .max(Comparator.comparing(Bid::getBidAmount))
                .orElse(null);
    }

    private Bid getFirstBid(List<Bid> bidList) {
        return bidList.getFirst();
    }

    private void createSocketForLot(SocketIOServer socketIOServer, Lot lot) {
//        eventListenerFactory.createDataListener(socketIOServer,lot.getLotId().toString());
    }

    @Async
    @Override
    public void sendNotificateToFollower(Lot lot){
        Set<SubscribeRequest> subscribeRequests = (Set<SubscribeRequest>) redisServiceImpl.getSetData("Notify_"+lot.getLotId().toString());
        if(subscribeRequests != null && !subscribeRequests.isEmpty()){
            for(SubscribeRequest subscribeRequest : subscribeRequests){
                String title = "Lot with id " + lot.getLotId() + " just ended!!";
                String body = "The auction for the lot you followed has just ended. Check the final bid and see if you won!";
                String token = subscribeRequest.getToken();
                fcmService.sendPushNotification(title, body, token);
            }
        }
    }
}

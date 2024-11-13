package swp.koi.service.bidService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import swp.koi.dto.request.AutoBidRequestDto;
import swp.koi.dto.request.BidRequestDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.LotRegisterStatusEnum;
import swp.koi.repository.*;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.mailService.EmailService;
import swp.koi.service.memberService.MemberServiceImpl;
import swp.koi.service.redisService.RedisService;
import swp.koi.service.redisService.RedisServiceImpl;
import swp.koi.service.socketIoService.EventListenerFactoryImpl;
import swp.koi.service.socketIoService.SocketDetail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final static String PREFIX_OF_BID = "Bid_history_";

    // Injecting the necessary repositories and services via constructor injection
    private final BidRepository bidRepository;
    private final MemberServiceImpl memberService;
    private final LotRepository lotRepository;
    private final LotRegisterRepository lotRegisterRepository;
    private final AccountRepository accountRepository;
    private final EventListenerFactoryImpl socketService;
    private final RedisServiceImpl redisServiceImpl;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void bid(BidRequestDto bidRequestDto) throws KoiException {
        // Retrieve the Member and Lot based on the DTO
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accountEmail = auth.getName();

        var account = accountRepository.findByEmail(accountEmail).orElseThrow(NoSuchElementException::new);

        Member member = memberService.getMemberByAccount(account);

        Lot lot = lotRepository.findById(bidRequestDto.getLotId())
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        // Validate the bid request to ensure it's eligible for placing a bid
        validateBidRequest(bidRequestDto, member, lot);

        
        // Create a Bid entity and update the Lot with the new bid
        Bid bid = createBid(bidRequestDto, member, lot);
        bidRepository.save(bid);

        //tech-debt here just check if auto-bid exist and handle exception based on the case
//        if(checkIfAutoBidderExistAndHaveHigherPrice(lot, bidRequestDto.getPrice())){
//
//            Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));
//
//            Member memberOfAutoBid = memberService.getMemberById(autoBidEntity.get().getMemberId());
//
//            float updatedPrice = autoBidderCanAffordNewPrice(lot, bidRequestDto.getPrice())
//                    ? bidRequestDto.getPrice() + lot.getStartingPrice() * 0.1f
//                    : autoBidEntity.get().getAmount();
//
//            Bid autoBid = Bid.builder()
//                    .bidAmount(updatedPrice)
//                    .member(memberOfAutoBid)
//                    .lot(lot)
//                    .build();
//
//            bidRepository.save(autoBid);
//            lot = updateLotWithSpecialType(updatedPrice, lot, memberOfAutoBid);
//
//        } else if (checkIfAutoBidderExistAndHaveLowerPrice(lot, bidRequestDto.getPrice())) {
//            Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));
//
//            String subject = "\uD83D\uDD34 Outbid. Raise your bid of "
//                    + autoBidEntity.get().getAmount()
//                    + " for lot with id " + lot.getLotId();
//
//            emailService.sendEmail("mentionable9999@gmail.com",subject,"idk bro");
//            redisServiceImpl.deleteData("Auto_bid_"+lot.getLotId().toString());
//            lot = updateLotWithSpecialType(bidRequestDto.getPrice(), lot, member);
//
//        } else {
//            lot = updateLotWithSpecialType(bidRequestDto.getPrice(), lot, member);
//        }

        lot = updateLotWithSpecialType(bidRequestDto.getPrice(), lot, member);
        updateDataOnClient(lot.getLotId(),bidRequestDto.getPrice(),account.getFirstName());

        lotRepository.save(lot);
    }



    /**
     * Updates data on the client side with the latest bid information.
     *
     * @param lotId      the lot ID
     * @param amount     the bid amount
     * @param bidderName the name of the bidder
     * @throws KoiException if any errors occur during the update
     */
    private void updateDataOnClient(int lotId, float amount, String bidderName) throws KoiException {

        SocketDetail socketDetail = SocketDetail.builder()
                .winnerName(bidderName)
                .newPrice(amount)
                .lotId(lotId)
                .build();

        socketService.sendDataToClient(socketDetail, String.valueOf(lotId));

    }

    /**
     * Lists all bids associated with a given lot ID.
     *
     * @param lotId the lot ID
     * @return a list of bids
     * @throws KoiException if no lot or bids are found
     */
    @Override
    public List<Bid> listBidByLotId(int lotId) throws KoiException {
        // Find the Lot by ID and fetch all bids associated with it
        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        return bidRepository.getBidByLot(lot)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND)); // Handle case where no bids are found
    }

    /**
     * Activates auto-bidding for a given lot.
     *
     * @param autoBidRequestDTO the auto-bid request data transfer object
     * @throws KoiException if any errors occur during the activation
     */
    @Override
    public void activeAutoBid(AutoBidRequestDto autoBidRequestDTO) throws KoiException {

        Lot lot = lotRepository.findById(autoBidRequestDTO.getLotId())
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        Member member = getUserInfoByUsingAuth.getMemberFromAuth();
        Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));

//        isMemberRegistered(member,lot); commented for testing

        float incrementPrice = lot.getStartingPrice() * 0.1f; // Example increment price calculation

        if (autoBidEntity.isEmpty()) {
            handleNoExistingAutoBid(autoBidRequestDTO, lot, member, incrementPrice);
        } else {
            handleExistingAutoBid(autoBidRequestDTO, lot, member, autoBidEntity.get(), incrementPrice);
        }


    }

    /**
     * Validates the bid request, ensuring the member is registered and that the bid price is valid.
     *
     * @param bidRequestDto the bid request data transfer object
     * @param member        the member placing the bid
     * @param lot           the lot being bid on
     * @throws KoiException if the bid request is invalid
     */
    // Validates the bid request, ensuring the member is registered and that the bid price is valid
    private void validateBidRequest(BidRequestDto bidRequestDto, Member member, Lot lot) throws KoiException {
        // Check if the member is registered for the lot and update their status if needed
        boolean isRegistered = lotRegisterRepository.findAllByLot(lot)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND))
                .stream()
                .anyMatch(lr -> lr.getMember().equals(member) && updateLotRegisterStatus(lr)); // Update status to "BIDDING"

        if (!isRegistered) {
            throw new KoiException(ResponseCode.MEMBER_NOT_REGISTERED_FOR_LOT);
        }

        if (LocalDateTime.now().isAfter(lot.getEndingTime()) || LocalDateTime.now().isBefore(lot.getStartingTime())) {
            throw new KoiException(ResponseCode.BID_TIME_PASSED);
        }

        if(bidRequestDto.getPrice() <= 0){
            throw new KoiException(ResponseCode.INVALID_BIDDING_AMOUNT);
        }

        AuctionType auctionType = lot.getAuctionType();

        switch (auctionType.getAuctionTypeName()) {
            case ASCENDING_BID: {

                List<Bid> bidList = bidRepository.getBidByLot(lot).orElse(null);
                if(bidList == null || bidList.isEmpty() && bidRequestDto.getPrice() == lot.getStartingPrice()) {
                    return;
                }

                if (bidRequestDto.getPrice() < lot.getCurrentPrice() + lot.getStartingPrice() * 0.1)
                    throw new KoiException((ResponseCode.BID_PRICE_TOO_LOW));
                return;
            }
            case SEALED_BID: {
                if (validateIfUserAlreadyBidded(member, lot)) {
                    throw new KoiException(ResponseCode.BID_SEALED_ALREADY);
                } else if(bidRequestDto.getPrice() < lot.getStartingPrice()) {
                    throw new KoiException((ResponseCode.BID_PRICE_TOO_LOW));
                }
                return;
            }
            case DESCENDING_BID: {
                if (!(bidRequestDto.getPrice() == lot.getCurrentPrice()))
                    throw new KoiException((ResponseCode.BID_PRICE_TOO_LOW));
                return;
            }
            case FIXED_PRICE_SALE: {
                if (bidRequestDto.getPrice() != lot.getCurrentPrice()) {
                    throw new KoiException((ResponseCode.BID_PRICE_TOO_LOW));
                } else if (validateIfUserAlreadyBidded(member, lot)) {
                    throw new KoiException(ResponseCode.BID_SEALED_ALREADY);
                }
                return;
            }
            default:
                throw new KoiException((ResponseCode.AUCTION_TYPE_NOT_FOUND));
        }
    }

    @Override
    public boolean isUserBidded(int lotId) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));
        Member member = getUserInfoByUsingAuth.getMemberFromAuth();
        return validateIfUserAlreadyBidded(member, lot);
    }

    @Override
    public Optional<Integer> countNumberOfPeopleWhoBidOnSpecificLot(int lotId){

        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        return bidRepository.countDistinctMemberByLot(lot);
    }
    /**
     * Checks if a member is registered for a given lot.
     *
     * @param member the member to check
     * @param lot    the lot to check
     * @throws KoiException if the member is not registered
     */
    private void isMemberRegistered(Member member,Lot lot) throws KoiException {

        boolean isRegistered = lotRegisterRepository.findAllByLot(lot)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND))
                .stream()
                .anyMatch(lr -> lr.getMember().equals(member));

        if(!isRegistered) {
            throw new KoiException(ResponseCode.MEMBER_NOT_REGISTERED_FOR_LOT);
        }
    }

    /**
     * Updates the status of the LotRegister to "BIDDING".
     *
     * @param lotRegister the lot register to update
     * @return true if the status was updated
     */
    private boolean updateLotRegisterStatus(LotRegister lotRegister) {
        lotRegister.setStatus(LotRegisterStatusEnum.BIDDING);
        return true; // Return true to indicate that the status was updated
    }

    /**
     * Creates a new Bid object with the provided bid request data.
     *
     * @param bidRequestDto the bid request data transfer object
     * @param member        the member placing the bid
     * @param lot           the lot being bid on
     * @return a new Bid object
     */
    private Bid createBid(BidRequestDto bidRequestDto, Member member, Lot lot) {
        return Bid.builder()
                .bidAmount(bidRequestDto.getPrice()) // Set bid amount
                .member(member) // Associate the bid with the member
                .lot(lot) // Associate the bid with the lot
                .build();
    }

    /**
     * Updates the lot with the ascending bid type.
     *
     * @param newPrice the new price of the bid
     * @param lot      the lot being updated
     * @param member   the member placing the bid
     * @return the updated lot
     */
    private Lot updateLotWithAscendingType(float newPrice, Lot lot, Member member) {

        lot.setCurrentPrice(newPrice);
        lot.setCurrentMemberId(member.getMemberId());
        Duration timeDifference = Duration.between(LocalDateTime.now(), lot.getEndingTime());
        //if ending time is less than 1 mins -> add another 1 mins to ending time
        if (timeDifference.toSeconds() <= 60) {
            lot.setEndingTime(lot.getEndingTime().plusMinutes(1));
        }
        return lot;
    }

    /**
     * Updates the lot with the descending bid type.
     *
     * @param newPrice the new price of the bid
     * @param lot      the lot being updated
     * @param member   the member placing the bid
     * @return the updated lot
     */
    private Lot updateLotWithDescendingType(float newPrice, Lot lot, Member member) {

        lot.setCurrentPrice(newPrice);
        lot.setCurrentMemberId(member.getMemberId());
        lot.setEndingTime(LocalDateTime.now());

        return lot;
    }


    /**
     * Updates the lot according to its auction type after validation.
     *
     * @param newPrice the new price of the bid
     * @param lot      the lot being updated
     * @param member   the member placing the bid
     * @return the updated lot
     */
    private Lot updateLotWithSpecialType(float newPrice, Lot lot, Member member) {
        return switch (lot.getAuctionType().getAuctionTypeName()) {
            case ASCENDING_BID -> updateLotWithAscendingType(newPrice, lot, member);
            case DESCENDING_BID -> updateLotWithDescendingType(newPrice, lot, member);
            default -> lot;
        };
    }

    /**
     * Validates if a member has already placed a sealed bid on a lot.
     *
     * @param member the member to check
     * @param lot    the lot to check
     * @return true if the member has already placed a sealed bid
     * @throws KoiException if the bid list is empty
     */
    private boolean validateIfUserAlreadyBidded(Member member, Lot lot) throws KoiException {

        List<Bid> bidList = bidRepository.getBidByLot(lot)
                .orElseThrow(() -> new KoiException(ResponseCode.BID_LIST_EMPTY));

        return bidList.stream().anyMatch(lr -> lr.getMember().equals(member));
    }


    /**
     * Retrieves the AutoBid entity for a given lot from Redis.
     *
     * @param lot the lot to retrieve the auto-bid for
     * @return the AutoBid entity
     */
    private AutoBid getAutoBidEntity(Lot lot) {
        return (AutoBid) redisServiceImpl.getData("Auto_bid_" + lot.getLotId());
    }

    /**
     * Checks if an auto-bidder exists and has a higher price than the current bid amount.
     *
     * @param lot       the lot being bid on
     * @param bidAmount the current bid amount
     * @return true if an auto-bidder exists and has a higher price
     */
    private boolean checkIfAutoBidderExistAndHaveHigherPrice(Lot lot, float bidAmount) {
        Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));
        return autoBidEntity.isPresent() && bidAmount <= autoBidEntity.get().getAmount();
    }

    /**
     * Checks if an auto-bidder can afford a new bid price.
     *
     * @param lot       the lot being bid on
     * @param bidAmount the current bid amount
     * @return true if the auto-bidder can afford the new price
     */
    private boolean autoBidderCanAffordNewPrice(Lot lot, float bidAmount) {
        Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));
        return autoBidEntity.isPresent()
                && autoBidEntity.get().getAmount() >= bidAmount + lot.getStartingPrice() * 0.1;
    }

    /**
     * Checks if an auto-bidder exists and has a lower price than the current bid amount.
     *
     * @param lot       the lot being bid on
     * @param bidAmount the current bid amount
     * @return true if an auto-bidder exists and has a lower price
     */
    private boolean checkIfAutoBidderExistAndHaveLowerPrice(Lot lot, float bidAmount){
        Optional<AutoBid> autoBidEntity = Optional.ofNullable(getAutoBidEntity(lot));
        return autoBidEntity.isPresent() && bidAmount > autoBidEntity.get().getAmount();
    }

    /**
     * Handles the case where no existing auto-bid exists for a lot.
     *
     * @param autoBidRequestDTO the auto-bid request data transfer object
     * @param lot               the lot of being auto-bid on
     * @param member            the member placing the auto-bid
     * @param incrementPrice    the increment price for the auto-bid
     */
    private void handleNoExistingAutoBid(AutoBidRequestDto autoBidRequestDTO,
                                         Lot lot, Member member,
                                         float incrementPrice) {
        float currentPrice = lot.getCurrentPrice();
        float minAutoBid = currentPrice + incrementPrice;

        if (lot.getBids().isEmpty()) {
            // Case 1.2: No one has bid before
            bidAndSaveAutoBid(member, lot, minAutoBid, autoBidRequestDTO.getAmount());
        } else {
            // Case 1.1: Someone already bid
            float newBidPrice = currentPrice + incrementPrice;
            bidAndSaveAutoBid(member, lot, newBidPrice, autoBidRequestDTO.getAmount());
        }
    }

    /**
     * Handles the case where an existing auto-bid exists for a lot.
     *
     * @param autoBidRequestDTO the auto-bid request data transfer object
     * @param lot               the lot of being auto-bid on
     * @param member            the member placing the auto-bid
     * @param existingAutoBid   the existing auto-bid
     * @param incrementPrice    the increment price for the auto-bid
     * @throws KoiException if the bid is outbid by the auto-bid
     */
    private void handleExistingAutoBid(AutoBidRequestDto autoBidRequestDTO,
                                       Lot lot, Member member,
                                       AutoBid existingAutoBid,
                                       float incrementPrice) {
        float newMaxPrice = autoBidRequestDTO.getAmount();
        float oldMaxPrice = existingAutoBid.getAmount();


        if(member.getMemberId().equals(existingAutoBid.getMemberId())){

            AutoBid autoBid = AutoBid.builder()
                    .amount(newMaxPrice)
                    .memberId(member.getMemberId())
                    .build();
            redisServiceImpl.saveDataWithoutTime("Auto_bid_" + lot.getLotId(), autoBid);

            return;
        }

        if (newMaxPrice > oldMaxPrice) {
            if (newMaxPrice >= oldMaxPrice + incrementPrice) {
                // Case 2.1a: New max price >= old max price + increment
                bidAndSaveAutoBid(member, lot, oldMaxPrice + incrementPrice, newMaxPrice);
            } else {
                // Case 2.1b: New max price < old max price + increment
                bidAndSaveAutoBid(member, lot, newMaxPrice, newMaxPrice);
            }
        } else {
            int autoBidderId = existingAutoBid.getMemberId();

            if (oldMaxPrice >= newMaxPrice + incrementPrice) {
                // Case 2.2a: Old max price >= new max price + increment
                logBid(newMaxPrice, member,lot);

                float outBidPrice = newMaxPrice + incrementPrice;
                activeOldAutoBid(autoBidderId,outBidPrice, lot);
            } else {
                // Case 2.2b: Old max price < new max price + increment
                logBid(newMaxPrice, member,lot);
                activeOldAutoBid(autoBidderId, oldMaxPrice, lot);
            }

            throw new KoiException(ResponseCode.BID_HAVE_BEEN_OUTBID_BY_AUTO);
        }
    }

    /**
     * Saves a bid and the corresponding auto-bid configuration.
     *
     * @param member           the member placing the bid
     * @param lot              the lot being bid on
     * @param bidAmount        the bid amount
     * @param maxAutoBidAmount the maximum auto-bid amount
     */
    private void bidAndSaveAutoBid(Member member, Lot lot, float bidAmount, float maxAutoBidAmount) {
        Bid bid = Bid.builder()
                .bidAmount(bidAmount)
                .member(member)
                .lot(lot)
                .build();
        bidRepository.save(bid);

        AutoBid autoBid = AutoBid.builder()
                .amount(maxAutoBidAmount)
                .memberId(member.getMemberId())
                .build();
        redisServiceImpl.saveDataWithoutTime("Auto_bid_" + lot.getLotId(), autoBid);

        Lot updatedLot = updateLotWithSpecialType(bidAmount, lot, member);
        lotRepository.save(updatedLot);
    }

    private void logBid(float bidAmount, Member member, Lot lot) {
        Bid bid = Bid.builder()
                .bidAmount(bidAmount)
                .member(member)
                .lot(lot)
                .bidTime(LocalDateTime.now())
                .build();
        bidRepository.save(bid);
    }

    private void activeOldAutoBid(int autoBidderId, float outBidPrice, Lot lot) {
        Member autoBidderMember = memberRepository.findById(autoBidderId)
                .orElseThrow(() -> new KoiException(ResponseCode.MEMBER_NOT_FOUND));

        logBid(outBidPrice, autoBidderMember, lot);

        Lot updatedLot = updateLotWithSpecialType(outBidPrice, lot, autoBidderMember);
        lotRepository.save(updatedLot);
    }
}

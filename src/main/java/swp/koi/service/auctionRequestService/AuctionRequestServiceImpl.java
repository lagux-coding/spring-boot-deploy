package swp.koi.service.auctionRequestService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import swp.koi.convert.KoiFishDtoToEntitConverter;
import swp.koi.dto.request.*;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.AccountRoleEnum;
import swp.koi.model.enums.AuctionRequestStatusEnum;
import swp.koi.model.enums.KoiFishStatusEnum;
import swp.koi.repository.AuctionRequestRepository;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.auctionTypeService.AuctionTypeService;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.jwtService.JwtService;
import swp.koi.service.koiBreederService.KoiBreederService;
import swp.koi.service.koiFishService.KoiFishService;
import swp.koi.service.mediaService.MediaService;
import swp.koi.service.transactionService.TransactionService;
import swp.koi.service.varietyService.VarietyService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionRequestServiceImpl implements AuctionRequestService{

    private final AuctionRequestRepository auctionRequestRepository;
    private final KoiBreederService koiBreederService;
    private final KoiFishService koiFishService;
    private final AccountService accountService;
    private final AuctionTypeService auctionTypeService;
    private final VarietyService varietyService;
    private final MediaService mediaService;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;
    private final TransactionService transactionService;

    @Override
    public AuctionRequest createRequest(AuctionRequestDto request) throws KoiException{
        try {
            Account account = accountService.findById(request.getAccountId());
            if(!account.getRole().equals(AccountRoleEnum.BREEDER))
                throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);
            // Create a new AuctionRequest object to hold the auction request data
            AuctionRequest auctionRequest = new AuctionRequest();

            // Retrieve the KoiFishDto and MediaDto from the incoming request
            KoiFishDto koiFishDTO = request.getKoiFish();
            MediaDto mediaDTO = request.getKoiFish().getMedia();

            // Get the KoiBreeder based on the provided breederId in the request
            KoiBreeder koiBreeder = koiBreederService.findByAccount(account);
            // Create a new KoiFish object using the data from the request and media information
            KoiFish koiFish = koiFishService.createKoiFishFromRequest(koiFishDTO, mediaDTO);

            // Set the retrieved KoiBreeder, auction request status, and KoiFish in the AuctionRequest object
            auctionRequest.setKoiBreeder(koiBreeder);
            auctionRequest.setStatus(AuctionRequestStatusEnum.REQUESTING);
            auctionRequest.setKoiFish(koiFish);

            // Save the AuctionRequest to the repository and return the saved instance
            return auctionRequestRepository.save(auctionRequest);
        } catch (KoiException e) {
            throw e;
        }
    }

    @Override
    public List<AuctionRequest> getAllAuctionRequest() {
        return auctionRequestRepository.findAll();
    }

    @Override
    public void assignStaffToRequest(Integer requestId, Integer accountId) throws KoiException{
        try{
            AuctionRequest request = auctionRequestRepository.findByRequestId(requestId)
                    .orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

            if(request.getStatus().equals(AuctionRequestStatusEnum.ASSIGNED))
                throw new KoiException(ResponseCode.ALREADY_HAVE_STAFF);

            Account account = accountService.findById(accountId);
            if(!account.getRole().equals(AccountRoleEnum.STAFF))
                throw new KoiException(ResponseCode.MUST_BE_STAFF);

            request.setAccount(account);
            request.setStatus(AuctionRequestStatusEnum.ASSIGNED);
            auctionRequestRepository.save(request);
        }catch (KoiException e){
            throw e;
        }
    }

    @Override
    public List<AuctionRequest> getAllStaffRequest(Integer accountId) throws KoiException{
        try{
            Account account = accountService.findById(accountId);
            if(!account.getRole().equals(AccountRoleEnum.STAFF))
                throw new KoiException(ResponseCode.MUST_BE_STAFF);
            List<AuctionRequest> list = auctionRequestRepository.findAllRequestByAccountId(accountId);
            return list;
        } catch (KoiException e) {
            throw e;
        }
    }

    @Override
    public List<AuctionRequest> getAllBreederRequest(Integer accountId) throws KoiException{
        try{
            Account account = accountService.findById(accountId);
            if(!account.getRole().equals(AccountRoleEnum.BREEDER))
                throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);

            List<AuctionRequest> list = auctionRequestRepository.findAllByBreederId(account.getKoiBreeder().getBreederId());
            return list;
        }catch (KoiException e){
            throw e;
        }
    }

    @Override
    public void breederCancelRequest(Integer requestId) throws KoiException{
        try{
            AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));
            if(auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.PENDING) ||
                    auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.WAITING)) {
                KoiFish koiFish = auctionRequest.getKoiFish();
                koiFish.setStatus(KoiFishStatusEnum.CANCELLED);
                koiFishService.saveFish(koiFish);
                auctionRequest.setStatus(AuctionRequestStatusEnum.CANCELED);
                auctionRequestRepository.save(auctionRequest);
            }
        }catch (KoiException e){
            throw e;
        }
    }

    @Transactional
    @Override
    public AuctionRequest updateRequest(Integer requestId, AuctionRequestUpdateDto dto) throws KoiException{
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();
        if(!account.getRole().equals(AccountRoleEnum.BREEDER))
            throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);

        KoiBreeder koiBreeder = koiBreederService.findByAccount(account);

        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

        if(auctionRequest.getKoiBreeder().getBreederId() != koiBreeder.getBreederId())
            throw new KoiException(ResponseCode.WRONG_BREEDER_REQUEST);

        if(auctionRequest.getKoiBreeder() == null){
            throw new KoiException(ResponseCode.BREEDER_NOT_FOUND);
        }

        KoiFish koiFish = koiFishService.findByFishId(auctionRequest.getKoiFish().getFishId());
        Variety variety = varietyService.findByVarietyName(dto.getKoiFish().getVarietyName());
        Media media = mediaService.findByMediaId(koiFish.getMedia().getMediaId());
        media.setImageUrl(dto.getKoiFish().getMedia().getImageUrl());
        media.setVideoUrl(dto.getKoiFish().getMedia().getVideoUrl());
        mediaService.save(media);
        AuctionType auctionType = auctionTypeService.findByAuctionTypeName(dto.getKoiFish().getAuctionTypeName());
        koiFish.setVariety(variety);
        koiFish.setGender(dto.getKoiFish().getGender());
        koiFish.setAge(dto.getKoiFish().getAge());
        koiFish.setSize(dto.getKoiFish().getSize());
        koiFish.setPrice(dto.getKoiFish().getPrice());
        koiFish.setAuctionType(auctionType);
        koiFish.setMedia(media);
        koiFishService.saveFish(koiFish);

        auctionRequest.setKoiFish(koiFish);
        return auctionRequestRepository.save(auctionRequest);
    }

    @Override
    public void changeStatus(Integer requestId, UpdateStatusDto request) {
        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getAccount().getRole().equals(AccountRoleEnum.STAFF)){
            auctionRequest.setStatus(request.getRequestStatus());
            auctionRequestRepository.save(auctionRequest);
        }
    }

    @Override
    public void negotiation(Integer requestId, AuctionRequestNegotiationDto request) throws KoiException{
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();

        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

        if(auctionRequest.getStatus().equals(AuctionRequestStatusEnum.CONFIRMING) ||
                auctionRequest.getStatus().equals(AuctionRequestStatusEnum.NEGOTIATING) ||
                auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.PENDING)){
            if(account.getRole().equals(AccountRoleEnum.MANAGER)){
                AuctionType auctionType = auctionTypeService.findByAuctionTypeName(request.getAuctionTypeName());
                auctionRequest.setOfferPrice(request.getPrice());
                auctionRequest.setAuctionType(auctionType);
                auctionRequest.setStatus(AuctionRequestStatusEnum.NEGOTIATING);
                auctionRequestRepository.save(auctionRequest);
            }else if(account.getRole().equals(AccountRoleEnum.BREEDER)){
                KoiFish koiFish = auctionRequest.getKoiFish();
                AuctionType auctionType = auctionTypeService.findByAuctionTypeName(request.getAuctionTypeName());
                koiFish.setPrice(request.getPrice());
                koiFish.setAuctionType(auctionType);
                koiFishService.saveFish(koiFish);
                auctionRequest.setStatus(AuctionRequestStatusEnum.CONFIRMING);
                auctionRequestRepository.save(auctionRequest);
            }else{
                throw new KoiException(ResponseCode.FAIL);
            }
        }else{
            throw new KoiException(ResponseCode.AUCTION_REQUEST_VALID_STATUS);
        }
    }

    @Override
    public void acceptNegotiation(Integer requestId) throws KoiException{
        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getStatus().equals(AuctionRequestStatusEnum.NEGOTIATING) && auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.PENDING)){
            KoiFish koiFish = auctionRequest.getKoiFish();
            AuctionType auctionType = auctionRequest.getAuctionType();
            koiFish.setPrice(auctionRequest.getOfferPrice());
            koiFish.setAuctionType(auctionType);
            koiFish.setStatus(KoiFishStatusEnum.WAITING);
            koiFishService.saveFish(koiFish);

            auctionRequest.setStatus(AuctionRequestStatusEnum.REGISTERED);
            auctionRequestRepository.save(auctionRequest);
        }else{
            throw new KoiException(ResponseCode.AUCTION_REQUEST_VALID_STATUS);
        }
    }

    @Override
    public void managerCancelRequest(Integer requestId) throws KoiException{
        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

        if(auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.PENDING) ||
                auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.WAITING)){
            KoiFish koiFish = auctionRequest.getKoiFish();
            koiFish.setStatus(KoiFishStatusEnum.CANCELLED);
            koiFishService.saveFish(koiFish);
            auctionRequest.setStatus(AuctionRequestStatusEnum.CANCELED);
            auctionRequestRepository.save(auctionRequest);
        }else{
            throw new KoiException(ResponseCode.AUCTION_REQUEST_VALID_STATUS);
        }
    }

    @Override
    public void managerAcceptRequest(Integer requestId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));
        if(auctionRequest.getStatus().equals(AuctionRequestStatusEnum.CONFIRMING) && auctionRequest.getKoiFish().getStatus().equals(KoiFishStatusEnum.PENDING)){
            KoiFish koiFish = auctionRequest.getKoiFish();
            koiFish.setStatus(KoiFishStatusEnum.WAITING);
            koiFishService.saveFish(koiFish);
            auctionRequest.setStatus(AuctionRequestStatusEnum.REGISTERED);
            auctionRequestRepository.save(auctionRequest);
        }else {
            throw new KoiException(ResponseCode.AUCTION_REQUEST_VALID_STATUS);
        }
    }

    @Override
    public AuctionRequest getRequestDetail(Integer requestId) throws KoiException{
        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(requestId).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));
        return auctionRequest;
    }

    @Override
    public void completePaymentForBreeder(Integer requestAuctionId) {
        AuctionRequest auctionRequest = auctionRequestRepository.findById(requestAuctionId)
                .orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

        auctionRequest.setStatus(AuctionRequestStatusEnum.PAID);
        auctionRequestRepository.save(auctionRequest);
        transactionService.createTransactionForBreederPayment(requestAuctionId);
    }


}

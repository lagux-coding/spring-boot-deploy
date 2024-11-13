package swp.koi.service.auctionRequestService;

import swp.koi.dto.request.*;
import swp.koi.exception.KoiException;
import swp.koi.model.AuctionRequest;

import java.util.List;

public interface AuctionRequestService {
    AuctionRequest createRequest(AuctionRequestDto request);

    List<AuctionRequest> getAllAuctionRequest();

    void assignStaffToRequest(Integer requestId, Integer accountId);

    List<AuctionRequest> getAllStaffRequest(Integer accountId);

    List<AuctionRequest> getAllBreederRequest(Integer accountId);

    void breederCancelRequest(Integer requestId);

    AuctionRequest updateRequest(Integer requestId, AuctionRequestUpdateDto auctionRequest);

    void changeStatus(Integer requestId, UpdateStatusDto request);

    void negotiation(Integer requestId, AuctionRequestNegotiationDto request) throws KoiException;

    void acceptNegotiation(Integer requestId) throws KoiException;

    void managerCancelRequest(Integer requestId);

    void managerAcceptRequest(Integer requestId);

    AuctionRequest getRequestDetail(Integer requestId);

    void completePaymentForBreeder(Integer requestAuctionId);

}

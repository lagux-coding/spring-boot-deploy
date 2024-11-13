package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.AccountEntityToDtoConverter;
import swp.koi.convert.AuctionRequestEntityToDtoConverter;
import swp.koi.dto.request.*;
import swp.koi.dto.response.*;
import swp.koi.exception.KoiException;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.auctionRequestService.AuctionRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "request", description = "Everything about your request")
public class AuctionRequestController {

    private final AuctionRequestService auctionRequestService;
    private final AuctionRequestEntityToDtoConverter auctionRequestEntityToDtoConverter;
    private final AccountEntityToDtoConverter accountEntityToDtoConverter;
    private final AccountService accountService;

    @Operation(summary = "Add a new request")
    @PostMapping("/breeder/request/addRequest")
    public ResponseData<AuctionRequestResponseDto> createRequest(@Valid @RequestBody AuctionRequestDto request){
        try{
            AuctionRequestResponseDto response = auctionRequestEntityToDtoConverter
                    .convertAuctionRequest(auctionRequestService.createRequest(request));
            return new ResponseData<>(ResponseCode.CREATED_SUCCESS, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Retrieve all request of a breeder", description = "Retrieve all request of a breeder by accountId")
    @GetMapping("/breeder/request/{accountId}")
    public ResponseData<List<AuctionRequestResponseDto>> getAllBreederRequest(@PathVariable Integer accountId){
        try{
            List<AuctionRequestResponseDto> response = auctionRequestEntityToDtoConverter.convertAuctionRequestList(auctionRequestService.getAllBreederRequest(accountId));
            return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Cancel a request")
    @PatchMapping("/breeder/request/cancel/{requestId}")
    public ResponseData<?> breederCancelRequest(@PathVariable Integer requestId){
        try{
            auctionRequestService.breederCancelRequest(requestId);
            return new ResponseData<>(ResponseCode.CANCEL_REQUEST_SUCCESS);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Update a request information", description = "Update request information by request id")
    @PutMapping("/breeder/request/update/{requestId}")
    public ResponseData<AuctionRequestResponseDto> updateRequest(@PathVariable Integer requestId,
                                                                 @RequestBody AuctionRequestUpdateDto auctionRequestUpdateDTO){
        try{
            AuctionRequestResponseDto response = auctionRequestEntityToDtoConverter.convertAuctionRequest(auctionRequestService.updateRequest(requestId, auctionRequestUpdateDTO));
           return new ResponseData<>(ResponseCode.UPDATE_REQUEST_SUCCESS, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Accept manager's negotiation")
    @PostMapping("/breeder/request/negotiation/accept/{requestId}")
    public ResponseData<?> acceptNegotiation(@PathVariable Integer requestId){
        try{
            auctionRequestService.acceptNegotiation(requestId);
            return new ResponseData<>(ResponseCode.SUCCESS);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Retrieve all request for manager")
    @GetMapping("/manager/request/getRequest")
    public ResponseData<List<AuctionRequestResponseDto>> getAllAuctionRequest(){
        List<AuctionRequestResponseDto> response = auctionRequestEntityToDtoConverter.convertAuctionRequestList(auctionRequestService.getAllAuctionRequest());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Retrieve all staff to assign a request")
    @GetMapping("/manager/request/assign-staff/getStaff")
    public ResponseData<List<AccountResponseDto>> getAllStaff(){
        List<AccountResponseDto> response = accountEntityToDtoConverter.convertAccountList(accountService.getAllStaff());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Assign a staff to a request")
    @PostMapping("/manager/request/assign-staff/{requestId}")
    public ResponseData<?> assignStaffToCheck(@PathVariable Integer requestId,
                                              @RequestParam Integer accountId){
        try{
            auctionRequestService.assignStaffToRequest(requestId, accountId);
            return new ResponseData<>(ResponseCode.STAFF_ASSIGN_SUCCESSFULLY);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Accept breeder's request")
    @PatchMapping("/manager/request/accept/{requestId}")
    public ResponseData<?> managerAcceptRequest(@PathVariable Integer requestId){
        auctionRequestService.managerAcceptRequest(requestId);
        return new ResponseData<>(ResponseCode.AUCTION_STATUS_CHANGE);
    }

    @Operation(summary = "Send negotiation")
    @PostMapping("/request/negotiation/{requestId}")
    public ResponseData<?> negotiation(@PathVariable Integer requestId, @RequestBody AuctionRequestNegotiationDto request){
        try{
            auctionRequestService.negotiation(requestId, request);
            return new ResponseData<>(ResponseCode.SUCCESS);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Cancel breeder's request")
    @PostMapping("/manager/request/cancel/{requestId}")
    public ResponseData<?> managerCancelRequest(@PathVariable Integer requestId){
        try{
            auctionRequestService.managerCancelRequest(requestId);
            return new ResponseData<>(ResponseCode.CANCEL_REQUEST_SUCCESS);
        } catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Retrieve all request that sent to staff")
    @GetMapping("/staff/list-request/{accountId}")
    public ResponseData<List<AuctionRequestResponseDto>> getAllStaffRequest(@PathVariable Integer accountId){
        try{
            List<AuctionRequestResponseDto> response = auctionRequestEntityToDtoConverter.convertAuctionRequestList(auctionRequestService.getAllStaffRequest(accountId));
            return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Update status for request")
    @PatchMapping("/staff/request/{requestId}/status")
    public ResponseData<?> changeStatus(@PathVariable Integer requestId, @RequestBody UpdateStatusDto request){
        auctionRequestService.changeStatus(requestId, request);
        return new ResponseData<>(ResponseCode.AUCTION_STATUS_CHANGE);
    }

    @Operation(summary = "Retrieve request's details")
    @GetMapping("/request/get/{requestId}")
    public ResponseData<AuctionRequestResponseDto> getRequestDetail(@PathVariable Integer requestId){
        try{
            AuctionRequestResponseDto response = auctionRequestEntityToDtoConverter.convertAuctionRequest(auctionRequestService.getRequestDetail(requestId));
            return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

}



package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.AccountEntityToDtoConverter;
import swp.koi.dto.response.AccountFullResponseDto;
import swp.koi.dto.response.LotRegisterResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.model.enums.LotRegisterStatusEnum;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.auctionRequestService.AuctionRequestService;
import swp.koi.service.lotRegisterService.LotRegisterService;

import java.util.List;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "manager", description = "Everything about your manager operation")
public class ManagerController {

    private final AccountEntityToDtoConverter accountEntityToDtoConverter;
    private final AccountService accountService;
    private final LotRegisterService lotRegisterService;
    private final AuctionRequestService auctionRequestService;

    @Operation(summary = "Retrieve all account")
    @GetMapping("/manager/get-all-account")
    public ResponseData<?> getAllAccount(){
        List<AccountFullResponseDto> response = accountEntityToDtoConverter
                .convertAccountFullList(accountService.getAllAccount());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Disable an account")
    @PatchMapping("/manager/disable-account")
    public ResponseData<?> disableAccount(@RequestParam Integer accountId){
        accountService.disableAccount(accountId);
        return new ResponseData<>(ResponseCode.DISABLE_SUCCESS);
    }

    @Operation(summary = "api to get list of member to refund")
    @GetMapping("/manager/refund-notificate")
    public ResponseData<List<LotRegisterResponseDto>> getListOfMemberToRefund(){
        List<LotRegisterResponseDto> lotRegisterList = lotRegisterService.findAllLotRegisWithStatus(LotRegisterStatusEnum.LOSE);
        return new ResponseData<>(ResponseCode.SUCCESS, lotRegisterList);
    }

    @Operation(summary = "api to pay breeder")
    @GetMapping("/manager/complete-payment-for-breeder")
    public ResponseData<?> updateStatusOfRequest(@RequestParam Integer requestAuctionId){

        auctionRequestService.completePaymentForBreeder(requestAuctionId);

        return new ResponseData<>(ResponseCode.SUCCESS);
    }

    @Operation(summary = "api to use after manager refund member")
    @GetMapping("/manager/refund")
    public ResponseData<?> refundToMember(@RequestParam Integer lotRegisterId){
        lotRegisterService.refundForMember(lotRegisterId);
        return new ResponseData<>(ResponseCode.SUCCESS);
    }
}

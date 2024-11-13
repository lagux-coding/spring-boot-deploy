package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.LotRegisterEntityToDtoConverter;
import swp.koi.dto.request.LotRegisterDto;

import swp.koi.dto.response.*;
import swp.koi.exception.KoiException;
import swp.koi.service.lotRegisterService.LotRegisterService;


import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/register-lot")
@RequiredArgsConstructor
@Tag(name = "lot-register", description = "Everything about your lot register")
public class LotRegisterController {

    private final LotRegisterService lotRegisterService;
    private final LotRegisterEntityToDtoConverter lotRegisterEntityToDtoConverter;

    @Operation(summary = "Register for the lot")
    @PostMapping("/regis")
    public ResponseData<?> registerLot(@RequestBody LotRegisterDto lotRegisterDTO) {
        try{
            var paymentLink = lotRegisterService.regisSlotWithLotId(lotRegisterDTO);
            if(paymentLink != null) {
                return new ResponseData<>(ResponseCode.SUCCESS, paymentLink);
            }
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }catch (UnsupportedEncodingException ex){
            return new ResponseData<>(ResponseCode.FAIL);
        }
        return null;
    }

    @Operation(summary = "Retrieve all lot register of a lot")
    @GetMapping("/list")
    public ResponseData<List<LotRegisterResponseDto>> listRegisterLotById(@RequestParam int lotId) {
        try{
            List<LotRegisterResponseDto> lotRegisterList = lotRegisterService.listLotRegistersByLotId(lotId);

            return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, lotRegisterList);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Retrieve winner of a lot")
    @GetMapping("/get-winner")
    public ResponseData<?> getLotWinner(@RequestParam Integer lotId){
        LotRegisterResponseDto response = lotRegisterEntityToDtoConverter.convertLotRegister(lotRegisterService.getLotWinner(lotId));
        return new ResponseData<>(ResponseCode.SUCCESS, response);
    }

    @Operation(summary = "Check if the member is registered or not")
    @GetMapping("/is-registered/{lotId}/{accountId}")
    public ResponseData<?> isRegistered(@PathVariable Integer lotId, @PathVariable Integer accountId){
        try{
            boolean isRegistered = lotRegisterService.isRegistered(lotId, accountId);
            if(isRegistered){
                return new ResponseData<>(ResponseCode.MEMBER_REGISTED);
            }else {
                return new ResponseData<>(ResponseCode.MEMBER_NOT_REGISTERED_FOR_LOT);
            }
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @GetMapping("/lot-register/get-all-deposited-lot-for-member")
    public ResponseData<?> getAllDepositedLotForMember(@RequestParam Integer accountId){
        List<LotRegisterForMemberResponseDto> response = lotRegisterEntityToDtoConverter.convertLotRegisterListForMember(lotRegisterService.getAllDepositedLotForMember(accountId));
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }
}



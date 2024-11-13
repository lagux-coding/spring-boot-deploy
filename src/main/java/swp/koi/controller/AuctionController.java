package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.AuctionEntityToDtoConverter;
import swp.koi.convert.KoiFishEntityToDtoConverter;
import swp.koi.convert.LotEntityToDtoConverter;
import swp.koi.dto.request.*;
import swp.koi.dto.response.*;
import swp.koi.exception.KoiException;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.auctionService.AuctionService;
import swp.koi.service.koiBreederService.KoiBreederService;
import swp.koi.service.koiFishService.KoiFishService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "auction", description = "Everything about your auction")
public class AuctionController {

    private final AuctionService auctionService;
    private final KoiFishService koiFishService;
    private final KoiFishEntityToDtoConverter koiFishEntityToDtoConverter;
    private final AuctionEntityToDtoConverter auctionEntityToDtoConverter;
    private final KoiBreederService koiBreederService;
    private final AccountService accountService;
    private final LotEntityToDtoConverter lotEntityToDtoConverter;

    @Operation(summary = "Retrieve all fish")
    @GetMapping("/manager/getFish")
    public ResponseData<?> getKoiFishFromApproveRequest(){
        List<KoiFishResponseDto> response = koiFishEntityToDtoConverter
                .convertFishList(koiFishService.getKoiFishFromRegisteredRequest());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Retrieve all fish that have same auction type with auction's auction type")
    @PostMapping("/manager/get-fish-auction")
    public ResponseData<List<KoiFishResponseDto>> getKoiFishBasedOnType(@RequestBody AuctionTypeDto auctionTypeDTO){
        List<KoiFishResponseDto> response = koiFishEntityToDtoConverter
                .convertFishList(koiFishService.getKoiFishBasedOnType(auctionTypeDTO));
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Create a auction")
    @PostMapping("/manager/createAuction")
    public ResponseData<?> createAuction(@Valid @RequestBody AuctionWithLotsDto request){
        try{
            AuctionResponseDto response = auctionService.createAuctionWithLots(request);
            return new ResponseData<>(ResponseCode.SUCCESS, response);
        }catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Create breeder account")
    @PostMapping("/manager/createBreeder")
    public ResponseData<KoiBreederResponseDto> createKoiBreeder(@Valid @RequestBody KoiBreederDto request){
        try{
            koiBreederService.createKoiBreeder(request);
            return new ResponseData<>(ResponseCode.CREATED_SUCCESS);
        } catch (KoiException e) {
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Create staff account")
    @PostMapping("/manager/createStaff")
    public ResponseData<?> createStaff(@RequestBody AccountRegisterDto staffDto){
        accountService.createAccountStaff(staffDto);
        return new ResponseData<>(ResponseCode.CREATED_SUCCESS);
    }

    @Operation(summary = "Retrieve all auction")
    @GetMapping("/auction/get-all-auction")
    public ResponseData<List<AuctionResponseDto>> getAllAuction(){
        List<AuctionResponseDto> response = auctionEntityToDtoConverter.converAuctiontList(auctionService.getAllAuction());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Retrieve details of an auction")
    @GetMapping("/auction/get-auction/{auctionId}")
    public ResponseData<AuctionResponseDto> getAuction(@PathVariable Integer auctionId){
        AuctionResponseDto response = auctionEntityToDtoConverter.convertAuction(auctionService.getAuction(auctionId));
        return new ResponseData<>(ResponseCode.SUCCESS, response);
    }

    @Operation(summary = "Retrieve details of a lot")
    @GetMapping("/auction/get-lot/{lotId}")
    public ResponseData<?> getLot(@PathVariable Integer lotId){
        LotResponseDto response = lotEntityToDtoConverter.convertLot(auctionService.getLot(lotId));
        return new ResponseData<>(ResponseCode.SUCCESS, response);
    }

    @Operation(summary = "Retrieve auctioning")
    @GetMapping("/auction/get-auction/auctioning")
    public ResponseData<List<AuctionResponseDto>> getAllOnGoingAuction(){
        List<AuctionResponseDto> response = auctionEntityToDtoConverter.converAuctiontList(auctionService.getAllOnGoingAuction());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Retrieve completed auction")
    @GetMapping("/auction/get-auction/completed")
    public ResponseData<List<AuctionResponseDto>> getAllCompletedAuction(){
        List<AuctionResponseDto> response = auctionEntityToDtoConverter.converAuctiontList(auctionService.getAllCompletedAuction());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

    @Operation(summary = "Retrieve waiting auction")
    @GetMapping("/auction/get-auction/waiting")
    public ResponseData<List<AuctionResponseDto>> getAllWaitingAuction(){
        List<AuctionResponseDto> response = auctionEntityToDtoConverter.converAuctiontList(auctionService.getAllWaitingAuction());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

}

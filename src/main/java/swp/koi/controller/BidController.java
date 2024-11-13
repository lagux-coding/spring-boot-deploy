package swp.koi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.BidEntityToDtoConverter;
import swp.koi.dto.request.AutoBidRequestDto;
import swp.koi.dto.request.BidRequestDto;
import swp.koi.dto.response.BidResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.exception.KoiException;
import swp.koi.service.bidService.BidService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
@Tag(name = "bid", description = "Everything about your bid")
public class BidController {

    private final BidService bidService;
    private final BidEntityToDtoConverter bidEntityToDtoConverter;

    @Operation(summary = "Bid in lot")
    @PostMapping("/bidAuction")
    public ResponseData<?> bidLotWithId(@RequestBody BidRequestDto bidRequestDto){
        try{
            bidService.bid(bidRequestDto);
            return new ResponseData<>(ResponseCode.BID_SUCCESS);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @Operation(summary = "Retrieve all bid in a lot")
    @GetMapping("/list")
    public ResponseData<List<BidResponseDto>> listBidByLotId(@RequestParam int lotId){
        try {
            List<BidResponseDto> response = bidEntityToDtoConverter.convertBidList(bidService.listBidByLotId(lotId));
            return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
        }catch (KoiException e){
            return new ResponseData<>(e.getResponseCode());
        }
    }

    @PostMapping("/active-auto-bid")
    public ResponseData<?> autoBidForLot(@RequestBody AutoBidRequestDto autoBidRequestDTO){

        try {
            bidService.activeAutoBid(autoBidRequestDTO);

            return new ResponseData<>(ResponseCode.SUCCESS);
        } catch (KoiException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/user-bidded")
    public ResponseData<?> isUserBidded(@RequestParam int lotId){
        boolean isUserBidded = bidService.isUserBidded(lotId);
        if(isUserBidded){
            return new ResponseData<>(ResponseCode.SUCCESS, "User bidded");
        }
        return new ResponseData<>(ResponseCode.SUCCESS, "User not bidded");
    }

    @PostMapping("/count")
    public ResponseData<?> countNumberOfPeopleWhoBidOnLot(@RequestParam int lotId){
        Optional<Integer> count = bidService.countNumberOfPeopleWhoBidOnSpecificLot(lotId);
        return new ResponseData<>(ResponseCode.SUCCESS, count.orElse(0));
    }
}

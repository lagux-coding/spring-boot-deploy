package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.convert.VarietyEntityToDtoConverter;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.dto.response.VarietyResponseDto;
import swp.koi.service.varietyService.VarietyService;

import java.util.List;

@RestController
@RequestMapping("/variety")
@RequiredArgsConstructor
@Tag(name = "variety", description = "Everything about your variety")
public class VarietyController {

    private final VarietyService varietyService;
    private final VarietyEntityToDtoConverter varietyEntityToDtoConverter;

    @Operation(summary = "Retrieve all variety")
    @GetMapping("/get-all-variety")
    public ResponseData<List<VarietyResponseDto>> getAllVariety(){
        List<VarietyResponseDto> response = varietyEntityToDtoConverter.convertVarietyList(varietyService.getAllVariety());
        return new ResponseData<>(ResponseCode.SUCCESS_GET_LIST, response);
    }

}

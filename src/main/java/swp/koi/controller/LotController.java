package swp.koi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.convert.LotEntityToDtoConverter;
import swp.koi.dto.response.LotResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.service.lotRegisterService.LotRegisterService;
import swp.koi.service.lotService.LotService;
import swp.koi.service.memberService.MemberService;

import java.util.List;

@RestController
@RequestMapping("/lot")
@RequiredArgsConstructor
@Tag(name = "lot", description = "Everything about your lot")
public class LotController {

    private final LotService lotService;
    private final LotRegisterService lotRegisterService;
    private final MemberService memberService;
    private final LotEntityToDtoConverter lotEntityToDtoConverter;

}

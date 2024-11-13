package swp.koi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.dto.response.ResponseData;
import swp.koi.service.koiFishService.KoiFishService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/koiFish")
@Tag(name = "koiFish", description = "Everything about your koiFish")
public class KoiFishController {

    private final KoiFishService koiFishService;

}

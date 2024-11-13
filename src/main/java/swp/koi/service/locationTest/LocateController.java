package swp.koi.service.locationTest;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;


@RestController("/locate-test")
@RequiredArgsConstructor
public class LocateController {

    private final NominatimService2 nominatimService2;



}

package swp.koi.service.locationTest;


import com.google.maps.errors.ApiException;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageForwardRequest;
import com.opencagedata.jopencage.model.JOpenCageLatLng;
import com.opencagedata.jopencage.model.JOpenCageResponse;
import com.opencagedata.jopencage.model.JOpenCageReverseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test-locate")
public class DistanceCalculator {
    private final GoogleMapService googleMapService;
    private final JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("8464e27eb7b5487cb13738ba4823bae9");

    @GetMapping("/address")
    public String getData(@RequestParam("address") String address){
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(address);
        request.setRestrictToCountryCode("vn");
        request.setBounds(102.14441,8.179066,109.464638,23.393395);
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition(); // get the coordinate pair of the first result
        System.out.println(firstResultLatLng.getLat().toString() + "," + firstResultLatLng.getLng().toString());

        return firstResultLatLng.getLat().toString();
    }

    @GetMapping("/reverse")
    public String reverseData(@RequestParam("lat") double lat,@RequestParam("lng") double lng){
        JOpenCageGeocoder jOCG = new JOpenCageGeocoder("8464e27eb7b5487cb13738ba4823bae9");

        JOpenCageReverseRequest req = new JOpenCageReverseRequest(lat, lng);
        req.setLanguage("vn"); // we want Spanish address format
        req.setLimit(5); // only return the first 5 results

        JOpenCageResponse res = jOpenCageGeocoder.reverse(req);

// get the formatted address of the first result:
        String fAddress = res.getResults().get(0).getFormatted();
        System.out.print(fAddress);

        return fAddress;
    }

    @GetMapping("/test")
    public void test() throws IOException, InterruptedException, ApiException {
        googleMapService.test();
    }
}

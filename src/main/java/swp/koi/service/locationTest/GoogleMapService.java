package swp.koi.service.locationTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapService {

    GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyCKMibT4DkZ9CfjsQSyvjgAYu3tcTVrQbw")
            .build();

    public void test() throws IOException, InterruptedException, ApiException {
        GeocodingResult[] response =  GeocodingApi.geocode(context,
                "Phường Thạnh Lộc, Quận 12, Thành phố Hồ Chí Minh, Việt Nam\n").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response[0].addressComponents));
    }
}

package swp.koi.service.locationTest;

import fr.dudie.nominatim.client.NominatimClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NominatimService2 {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public void findLocate(String location) {
        OkHttpClient client = new OkHttpClient();
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = NOMINATIM_URL + "?q=" + encodedLocation + "&format=json&addressdetails=1";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "maihailongviet@gmail.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                System.out.println(response.body().string());
            } else {
                System.err.println("Request failed: " + response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


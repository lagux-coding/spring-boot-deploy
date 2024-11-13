package swp.koi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FirebaseInitializer {

    String firebaseFilePath = System.getenv("FIREBASE_FILE");

    @Bean
    public FirebaseApp firebaseApp() throws IOException, FileNotFoundException {

        if (firebaseFilePath == null || firebaseFilePath.isEmpty()) {
            System.out.println("FIREBASE_CREDENTIALS environment variable is not set.");
            throw new IllegalArgumentException("FIREBASE_CREDENTIALS environment variable is not set.");
        }

        FileInputStream serviceAccount =
                new FileInputStream(firebaseFilePath);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("swptest-7f1bb")
                .build();

        return FirebaseApp.initializeApp(options);
    }
}


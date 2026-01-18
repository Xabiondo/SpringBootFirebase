package org.garcia.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FirebaseService {

    private Firestore db;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
            try (InputStream serviceAccount = resource.getInputStream()) {

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }

            this.db = FirestoreClient.getFirestore();

        } catch (Exception e) {
            throw new RuntimeException("Error inicializando Firebase Admin SDK. Revisa serviceAccountKey.json", e);
        }
    }

    public Firestore db() {
        return db;
    }
}
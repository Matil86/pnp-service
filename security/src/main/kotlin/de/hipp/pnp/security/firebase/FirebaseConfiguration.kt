package de.hipp.pnp.security.firebase

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseConfiguration {

    @Bean
    fun firebaseApp(): FirebaseApp = FirebaseApp.initializeApp()

    @Bean
    fun fireStore(): Firestore =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .build()
            .service
}
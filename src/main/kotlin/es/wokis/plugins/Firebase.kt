package es.wokis.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import java.io.FileInputStream


fun Application.configureFirebase() {
    val fileDirectory = config.getString("firebaseSdkDir")

    val serviceAccount = FileInputStream(fileDirectory)

    val options = FirebaseOptions
        .builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
}
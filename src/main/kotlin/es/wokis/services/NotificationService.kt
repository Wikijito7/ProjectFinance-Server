package es.wokis.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import es.wokis.data.bo.user.UserBO


fun sendNotifications(users: List<UserBO>) {
    val registrationTokens: List<String> = users.flatMap { it.devices }

    val message = MulticastMessage.builder()
        .putData("score", "850")
        .putData("time", "2:45")
        .addAllTokens(registrationTokens)
        .build()
    val response = FirebaseMessaging.getInstance().sendMulticast(message)
    if (response.failureCount > 0) {
        val responses = response.responses
        val failedTokens: List<String> = responses.mapIndexedNotNull { index, sendResponse ->
            if (!sendResponse.isSuccessful) registrationTokens[index] else null
        }
        println("List of tokens that caused failures: $failedTokens")
    }
}

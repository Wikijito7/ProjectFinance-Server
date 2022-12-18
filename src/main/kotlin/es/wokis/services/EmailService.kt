package es.wokis.services

import es.wokis.data.bo.user.UserBO
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.plugins.config
import es.wokis.utils.HashGenerator
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService {
    private val fromEmail = config.getString("mail.user")
    private val fromPassword = config.getString("mail.pass")

    private val about = "Invitacion"

    fun sendEmail(user: UserBO): VerificationBO {
        val emailHtml = this::class.java.getResource("/emails/${user.lang}/email-verify.html") ?: throw IllegalAccessException()

        val properties: Properties = System.getProperties().apply {
            put("mail.smtp.host", "email.wokis.es")
            put("mail.smtp.user", fromEmail)
            put("mail.smtp.clave", fromPassword)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.ssl.trust", "email.wokis.es");
            put("mail.smtp.port", 587)
        }
        val hash = HashGenerator.generateHash(20)
        val cuerpo = emailHtml.readText()

        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)

        try {
            with(message) {
                setFrom(InternetAddress(fromEmail))
                addRecipients(Message.RecipientType.TO, user.email)
                subject = about
                setContent(cuerpo, "text/html")
            }

            with(session.getTransport("smtp")) {
                connect("email.wokis.es", fromEmail, fromPassword)
                sendMessage(message, message.allRecipients)
                close()
            }

        } catch (e: MessagingException) {
            println(e.message)
        }

        return VerificationBO(
            email = user.email,
            verificationToken = hash
        )
    }
}
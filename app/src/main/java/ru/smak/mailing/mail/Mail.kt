package ru.smak.mailing.mail

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Multipart
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import java.util.Date
import java.util.Properties

class Mail : Authenticator() {
    suspend fun send(
        receiver: String,
        subject: String,
        content: String,
    ){
        val props = Properties()
        props["mail.smtp.host"] = "smtp.yandex.ru"
        props["mail.smtp.port"] = "465"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"] = "false"
        props.setProperty("mail.smtp.quitwait", "false")
        val session: Session = Session.getInstance(props, this@Mail)
        try {
            val msg = MimeMessage(session)
            msg.setFrom(InternetAddress("smak-80@yandex.ru"))
            val address: Array<InternetAddress> = arrayOf(InternetAddress(receiver))
            msg.setRecipients(Message.RecipientType.TO, address)
            msg.subject = subject
            msg.sentDate = Date()

            val textBp = MimeBodyPart()
            textBp.setText(content, "utf-8")

//            val att = MimeBodyPart()
//            att.setText(content, "UTF-8", "plain")
//            att.addHeader("Content-Type", "text/plain; charset=UTF-8")
//            att.setFileName("logs.txt")

            val mp: Multipart = MimeMultipart()
            mp.addBodyPart(textBp)
//            mp.addBodyPart(att)

            // add the Multipart to the message
            msg.setContent(mp)

            // send the message
            Transport.send(msg)
        } catch (mex: MessagingException) {
            mex.printStackTrace()
            var ex: Exception?
            if (mex.nextException.also { ex = it } != null) {
                ex!!.printStackTrace()
            }
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication =
        PasswordAuthentication("smak-80", "")
}
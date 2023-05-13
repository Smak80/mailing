package ru.smak.mailing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.smak.mailing.mail.Mail

class SmsReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.compareTo("android.provider.Telephony.SMS_RECEIVED", true) == 0) {
            context?.run{
                val tel = getSystemService(TelephonyManager::class.java)
                val smss = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                smss?.let{
                    val subject = it[0].displayOriginatingAddress
                    val msg = it.fold(""){ full, part -> "$full${part.displayMessageBody}" }
                    scope.launch {
                        Mail().send("smak-80@yandex.ru", subject, msg)
                    }
                }
            }
        }
    }

}
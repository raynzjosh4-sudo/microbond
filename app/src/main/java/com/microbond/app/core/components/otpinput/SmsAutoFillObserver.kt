package com.microbond.app.core.components.otpinput

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Composable observer that registers an SMS broadcast receiver to automatically
 * detect incoming SMS messages and extract 6-digit verification codes.
 */
@Composable
fun SmsAutoFillObserver(
    enabled: Boolean = true,
    onCodeReceived: (String) -> Unit
) {
    if (!enabled) return

    val context = LocalContext.current

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                    try {
                        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        for (sms in messages) {
                            val body = sms.displayMessageBody ?: continue
                            val isRelevant = body.contains("microbond", ignoreCase = true) ||
                                    body.contains("code", ignoreCase = true) ||
                                    body.contains("otp", ignoreCase = true) ||
                                    body.contains("verification", ignoreCase = true)
                            if (isRelevant) {
                                val match = Regex("""\b(\d{6})\b""").find(body)
                                if (match != null) {
                                    val otp = match.value
                                    onCodeReceived(otp)
                                    break
                                }
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        try {
            context.registerReceiver(receiver, filter)
        } catch (_: Exception) {
        }

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
        }
    }
}

package com.paula.seniorcare_app

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        /*Looper.prepare()
        Handler().post {
            Toast.makeText(baseContext, message.notification?.title, Toast.LENGTH_LONG).show()
        }
        Looper.loop()*/
    }
}
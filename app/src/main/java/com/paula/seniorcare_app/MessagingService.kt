package com.paula.seniorcare_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = 0
const val channelName = "CHANNEL_NAME"

class MessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(message.data.isNotEmpty()){
            val data: Map<String,String> = message.data
            if(data.get("type") == "alert") {
                sendNotificationAlert("Alerta", data)
                val intent = Intent(baseContext, ShowAlertActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("msg", data.get("msg"))
                intent.putExtra("time", data.get("time"))
                intent.putExtra("date", data.get("date"))
                startActivity(intent)
            }
            //if tipo de mensaje de data == LLAMADA ACEPTADA
            //  FCM LLAMADA ACEPTADA
            //if tipo de mensaje de data == LLAMADA RECHAZADA
            //  FCM LLAMADA RECHAZADA
            //if tipo de mensaje de data == LLAMADA PENDIENTE
            //  FCM LLAMADA PENDIENTE
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(currentUid).update("token",token)
    }

    private fun sendNotificationAlert(title: String, data: Map<String, String>){
        val intent = Intent(this, ShowAlertActivity::class.java) //Pantalla de alertas o de llamada
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("msg", data.get("msg"))
        intent.putExtra("time", data.get("time"))   //Se pone una hora que no es
        intent.putExtra("date", data.get("date"))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelName, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelName)
                .setSmallIcon(R.drawable.icono_logo_transparent)
                .setContentTitle(title)
                .setContentText(data.get("msg"))
                .setAutoCancel(true)
                //.setVisibility(VISIBILITY_PUBLIC)
                //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                //.setCategory(NotificationCompat.CATEGORY_ALARM)
                //.setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)

            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(channelId, builder.build())
        }
    }
}
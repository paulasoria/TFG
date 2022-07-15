package com.paula.seniorcare_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
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
            if(data["type"] == "alert") {
                sendNotificationAlert("Alerta", data)
                val intent = Intent(baseContext, ShowAlertActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("msg", data["msg"])
                intent.putExtra("time", data["time"])
                intent.putExtra("date", data["date"])
                startActivity(intent)
            } else if(data["type"] == "incomingCall") {
                //sendNotificationIncomingCall();
                val intent = Intent(baseContext, IncomingVideocallActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("senderName", data["senderName"])
                intent.putExtra("senderEmail", data["senderEmail"])
                intent.putExtra("senderImage", data["senderImage"])
                intent.putExtra("callId", data["callId"])
                startActivity(intent)
            } else if(data["type"] == "acceptedCall") {
                //Llamada aceptada
                val intent = Intent(baseContext, VideocallActivity::class.java)
                //intent.putExtra() //Datos de los usuarios de la videollamada
                startActivity(intent)
            }  else if(data["type"] == "rejectedCall"){
                val intent = Intent(baseContext, HomeActivity::class.java)
                startActivity(intent)
            }
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
        intent.putExtra("msg", data["msg"])
        intent.putExtra("time", data["time"])   //Se pone una hora que no es
        intent.putExtra("date", data["date"])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelName, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelName)
                .setSmallIcon(R.drawable.icono_logo_transparent)
                .setContentTitle(title)
                .setContentText(data["msg"])
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
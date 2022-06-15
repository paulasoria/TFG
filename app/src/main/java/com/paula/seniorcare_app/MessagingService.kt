package com.paula.seniorcare_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "DEFAULT_CHANNEL_ID"
class MessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(message.data.isNotEmpty()){
            val data: Map<String,String> = message.data
            //if tipo de mensaje de data == LLAMADA ACEPTADA
            //  FCM LLAMADA ACEPTADA
            //if tipo de mensaje de data == LLAMADA RECHAZADA
            //  FCM LLAMADA RECHAZADA
            //if tipo de mensaje de data == ALERTA
            //  ALERTA?
            //if tipo de mensaje de data == LLAMADA PENDIENTE
            //  LLAMADA?
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

    /*private fun sendNotification(message: String){
        val intent = Intent(this, ::class.java) //Pantalla de alertas o de llamada
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icono_logo_transparent)
            .setContentTitle(TITULO)    //Crear titulo en funcion del tipo de mensaje en data
            .setContentText(MENSAJE)    //Crear mensaje en funcion del tipo de mensaje en data + valores
            .setAutoCancel(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            //.setCategory(NotificationCompat.CATEGORY_ALARM)
            //.setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
                //.apply {
                //      description?
                // }
            notificationManager.createNotificationChannel(channel)
        }
        /*id de la notificacion, que hay que guardar para después actualizarla o quitarla*/
        notificationManager.notify(0, builder.build())
    }*/
}
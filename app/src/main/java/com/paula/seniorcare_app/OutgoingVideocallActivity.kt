package com.paula.seniorcare_app

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_outgoing_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class OutgoingVideocallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_videocall)

        var callId: String? = null

        val receiverUid = intent.getStringExtra("uid").toString()
        val receiverImage = intent.getStringExtra("image").toString()
        val receiverName = intent.getStringExtra("name").toString()

        nameVideocallTextView.text = receiverName
        emailVideocallTextView.text = intent.getStringExtra("email").toString()
        Glide.with(this).load(receiverImage).centerCrop().into(imageVideocallImageView)

        val db = FirebaseFirestore.getInstance()
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val time = if(minute.toString().length == 1){
            "$hour:0$minute"
        }else {
            "$hour:$minute"
        }
        val timestamp = FieldValue.serverTimestamp()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val senderName = getUser(db, FirebaseAuth.getInstance().currentUser!!.uid)?.get("name") as String?
                callId = createCall(db, receiverUid, receiverName, senderName, date, time, "waiting", timestamp)
            }
        }

        endCallButton.setOnClickListener{
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    changeStateCall(db, callId!!, "lost")
                }
                finish()
            }
            //Llamada perdida ¿notificacion?
        }
    }

    private suspend fun createCall(db: FirebaseFirestore, receiver: String, receiverName: String, senderName: String?, date: String?, time: String?, state: String, timestamp: FieldValue): String? {
        return try {
            val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("videocalls").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "receiverName" to receiverName, "senderName" to senderName, "date" to date, "time" to time, "state" to state, "timestamp" to timestamp)).await()
            id
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "CREATING VIDEOCALL IN DATABASE ERROR", e)
            null
        }
    }

    private suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "GETTING USER NAME ERROR", e)
            null
        }
    }

    private suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return try {
            db.collection("videocalls").document(callId).update("state", state).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "UPDATING VIDEOCALL IN DATABASE ERROR", e)
            false
        }
    }
}
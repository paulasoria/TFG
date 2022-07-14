package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_incoming_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class IncomingVideocallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)
        val db = FirebaseFirestore.getInstance()

        nameVideocallTextView.text = intent.getStringExtra("senderName").toString()
        emailVideocallTextView.text = intent.getStringExtra("senderEmail").toString()
        val senderImage = intent.getStringExtra("senderImage").toString()
        Glide.with(this).load(senderImage).centerCrop().into(imageVideocallImageView)
        val callId = intent.getStringExtra("callId").toString()

        rejectCallButton.setOnClickListener{
            finish()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    changeStateCall(db, callId, "rejected")
                }
            }
            //Notificar al otro usuario que se ha finalizado la llamada
        }

        acceptCallButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    changeStateCall(db, callId, "accepted")
                }
            }
            //Notificar al otro usuario que se ha aceptado la llamada y pasar a la videollamada
        }
    }

    private suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return try{
            db.collection("videocalls").document(callId).update("state", state).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
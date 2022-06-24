package com.paula.seniorcare_app

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
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

        val receiverUid = intent.getStringExtra("uid").toString()
        val receiverName = intent.getStringExtra("name").toString()
        val receiverEmail = intent.getStringExtra("email").toString()
        val receiverImage = intent.getStringExtra("image").toString()

        nameVideocallTextView.text = receiverName
        Glide.with(this).load(receiverImage).centerCrop().into(imageVideocallImageView)

        val db = FirebaseFirestore.getInstance()
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
        val time = "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                createVideocallInDatabase(db, receiverUid, date, time, "waiting")
            }
        }

        endCallButton.setOnClickListener{
            finish()
            //Modificar videollamada en firestore con estado "lost"
            //Google Cloud Function para poner llamada como "lost" en el otro usuario
        }
    }

    private suspend fun createVideocallInDatabase(db: FirebaseFirestore, receiver: String, date: String?, time: String?, state: String): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("videocalls").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "date" to date, "time" to time, "state" to state)).await()
            //Enviar notificacion
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "CREATING VIDEOCALL IN DATABASE ERROR", e)
            false
        }
    }
}
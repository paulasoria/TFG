package com.paula.seniorcare_app

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

        var videocallId: String? = null

        val receiverUid = intent.getStringExtra("uid").toString()
        val receiverImage = intent.getStringExtra("image").toString()
        val receiverName = intent.getStringExtra("name").toString()

        nameVideocallTextView.text = receiverName
        emailVideocallTextView.text = intent.getStringExtra("email").toString()
        Glide.with(this).load(receiverImage).centerCrop().into(imageVideocallImageView)

        val db = FirebaseFirestore.getInstance()
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
        val time = "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}"
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val senderName = getUser(db, FirebaseAuth.getInstance().currentUser!!.uid)?.get("name") as String?
                videocallId = createVideocallInDatabase(db, receiverUid, receiverName, senderName, date, time, "waiting")
            }
        }

        endCallButton.setOnClickListener{
            finish()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    modifyVideocallInDatabase(db, videocallId!!, "lost")
                }
            }
        }
    }

    private suspend fun createVideocallInDatabase(db: FirebaseFirestore, receiver: String, receiverName: String, senderName: String?, date: String?, time: String?, state: String): String? {
        return try {
            val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("videocalls").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "receiverName" to receiverName, "senderName" to senderName, "date" to date, "time" to time, "state" to state)).await()
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

    private suspend fun modifyVideocallInDatabase(db: FirebaseFirestore, id: String, state: String): Boolean {
        return try {
            db.collection("videocalls").document(id).update("state", state).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "UPDATING VIDEOCALL IN DATABASE ERROR", e)
            false
        }
    }
}
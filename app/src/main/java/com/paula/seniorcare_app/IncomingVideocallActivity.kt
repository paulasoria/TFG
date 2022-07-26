package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_incoming_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class IncomingVideocallActivity : AppCompatActivity() {
    var functions: FirebaseFunctions = Firebase.functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)
        val db = FirebaseFirestore.getInstance()

        nameVideocallTextView.text = intent.getStringExtra("senderName").toString()
        emailVideocallTextView.text = intent.getStringExtra("senderEmail").toString()
        val senderImage = intent.getStringExtra("senderImage").toString()
        Glide.with(this).load(senderImage).centerCrop().into(imageVideocallImageView)
        val senderUid = intent.getStringExtra("senderUid").toString()
        val callId = intent.getStringExtra("callId").toString()
        val receiverJwt = intent.getStringExtra("receiverJwt").toString()

        rejectCallButton.setOnClickListener{
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    changeStateCall(db, callId, "rejected")
                    //HTTP FUNCTION
                    val sender = getUser(db, senderUid)
                    val senderToken = sender?.get("token") as String
                    rejectCallHttp(senderToken, callId)
                }
                finish()
            }
            //Notificar al otro usuario que se ha finalizado la llamada
        }

        acceptCallButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    changeStateCall(db, callId, "accepted")
                    //HTTP FUNCTION
                    val sender = getUser(db, senderUid)
                    val senderToken = sender?.get("token") as String    //Dice que es null ???
                    acceptCallHttp(senderToken, callId)
                }
                val intent = Intent(baseContext, VideocallActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
                val edit: SharedPreferences.Editor = sp.edit()
                edit.putString("JWT",receiverJwt)
                edit.apply()
                intent.putExtra("callId",callId)
                startActivity(intent)
            }
            //Notificar al otro usuario que se ha aceptado la llamada y pasar a la videollamada
        }
    }

    private fun rejectCallHttp(token: String, callId: String): Task<String> {
        val data = hashMapOf(
            "token" to token,
            "callId" to callId
        )

        return functions
            .getHttpsCallable("rejectVideocall")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    private fun acceptCallHttp(token: String, callId: String): Task<String> {
        val data = hashMapOf(
            "token" to token,
            "callId" to callId
        )

        return functions
            .getHttpsCallable("acceptVideocall")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
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
        return try{
            db.collection("videocalls").document(callId).update("state", state).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
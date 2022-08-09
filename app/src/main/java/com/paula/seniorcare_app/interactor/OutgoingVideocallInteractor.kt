package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.paula.seniorcare_app.contract.OutgoingVideocallContract
import kotlinx.coroutines.tasks.await
import java.util.*

class OutgoingVideocallInteractor: OutgoingVideocallContract.Interactor {
    override fun rejectCallHttp(token: String, callId: String): Task<String> {
        val functions: FirebaseFunctions = Firebase.functions
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

    override fun createCallHttp(senderUid: String, senderName: String, senderEmail: String, senderImage: String, receiverUid: String, receiverName: String, receiverEmail: String, receiverImage: String, receiverToken: String, callId: String): Task<String> {
        val functions: FirebaseFunctions = Firebase.functions
        val data = hashMapOf(
            "senderUid" to senderUid,
            "senderName" to senderName,
            "senderEmail" to senderEmail,
            "senderImage" to senderImage,
            "receiverUid" to receiverUid,
            "receiverName" to receiverName,
            "receiverEmail" to receiverEmail,
            "receiverImage" to receiverImage,
            "receiverToken" to receiverToken,
            "callId" to callId
        )

        return functions
            .getHttpsCallable("createVideocall")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    override suspend fun createCall(db: FirebaseFirestore, receiver: String, receiverName: String, senderName: String?, date: String?, time: String?, state: String, timestamp: FieldValue): String? {
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

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "GETTING USER NAME ERROR", e)
            null
        }
    }

    override suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return try {
            db.collection("videocalls").document(callId).update("state", state).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "UPDATING VIDEOCALL IN DATABASE ERROR", e)
            false
        }
    }
}
package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.paula.seniorcare_app.contract.IncomingVideocallContract
import kotlinx.coroutines.tasks.await

class IncomingVideocallInteractor: IncomingVideocallContract.Interactor {
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

    override fun acceptCallHttp(token: String, callId: String): Task<String> {
        val functions: FirebaseFunctions = Firebase.functions
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
        return try{
            db.collection("videocalls").document(callId).update("state", state).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CHANGING STATE CALL ERROR", e)
            false
        }
    }
}
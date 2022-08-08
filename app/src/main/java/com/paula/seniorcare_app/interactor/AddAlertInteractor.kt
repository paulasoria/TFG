package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddAlertContract
import kotlinx.coroutines.tasks.await
import java.util.*

class AddAlertInteractor: AddAlertContract.Interactor {
    override suspend fun getAddedRelativesList(db: FirebaseFirestore) : QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING ADDED RELATIVES LIST ERROR", e)
            null
        }
    }

    override suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            return db.collection("users").document(relativeUid).collection("managers").document(currentUid.toString()).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS MANAGER ERROR", e)
            false
        }
    }

    override suspend fun getReceiverOfAlert(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return try {
            val user = db.collection("users").whereEqualTo("email", email).get().await()
            user
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "GETTING RECEIVER OF ALERT ERROR", e)
            null
        }
    }

    override suspend fun createAlertInDatabase(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(sender).collection("alerts").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "tag" to tag, "repetition" to repetition, "time" to time, "daysOfWeek" to daysOfWeek, "date" to date)).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "CREATING ALERT IN DATABASE ERROR", e)
            false
        }
    }

    override suspend fun updateAlertInDatabase(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return try {
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(sender).collection("alerts").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "tag" to tag, "repetition" to repetition, "time" to time, "daysOfWeek" to daysOfWeek, "date" to date)).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "UPDATING ALERT IN DATABASE ERROR", e)
            false
        }
    }
}
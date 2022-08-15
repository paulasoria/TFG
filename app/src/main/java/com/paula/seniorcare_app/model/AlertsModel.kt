package com.paula.seniorcare_app.model

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class AlertsModel {
    /**
     * Gets the receiver of an alert searching by the email
     *
     * @param db
     * @param email
     * @return
     */
    suspend fun getReceiverOfAlertEmail(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return try {
            val user = db.collection("users").whereEqualTo("email", email).get().await()
            user
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "GETTING RECEIVER OF ALERT ERROR", e)
            null
        }
    }

    /**
     * Gets the receiver of an alert searching by the uid
     *
     * @param db
     * @param uid
     * @return
     */
    suspend fun getReceiverOfAlertUid(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try{
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING RECEIVER OF ALERT ERROR", e)
            null
        }
    }

    /**
     * Creates an alert on the database using the data provided by the user
     *
     * @param db
     * @param receiver
     * @param tag
     * @param repetition
     * @param time
     * @param daysOfWeek
     * @param date
     * @return
     */
    suspend fun createAlert(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
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

    /**
     * Updates an alert on the database with the data provided by the user
     *
     * @param db
     * @param id
     * @param receiver
     * @param tag
     * @param repetition
     * @param time
     * @param daysOfWeek
     * @param date
     * @return
     */
    suspend fun updateAlert(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return try {
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(sender).collection("alerts").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "tag" to tag, "repetition" to repetition, "time" to time, "daysOfWeek" to daysOfWeek, "date" to date)).await()
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "UPDATING ALERT IN DATABASE ERROR", e)
            false
        }
    }

    /**
     * Gets the configured alerts by the user from the database
     *
     * @param db
     * @return
     */
    suspend fun getConfiguredAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString())
                .collection("alerts").whereEqualTo("sender",currentUid.toString()).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING CONFIGURED ALERTS ERROR", e)
            null
        }
    }

    /**
     * Gets the history of alerts from the database
     *
     * @param db
     * @return
     */
    suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("historyAlerts").orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF ALERTS ERROR", e)
            null
        }
    }
}
package com.paula.seniorcare_app.model

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*

class PetitionsModel {
    /**
     * Gets the pending petitions of the user
     *
     * @param db
     * @param uid
     * @return
     */
    suspend fun getPendingPetitions(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return try {
            val data = db.collection("users").document(uid).collection("petitions").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING PENDING PETITIONS ERROR", e)
            null
        }
    }

    /**
     * Gets the sender of a petition
     *
     * @param db
     * @param uid
     * @return
     */
    suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try{
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING SENDER OF PETITION ERROR", e)
            null
        }
    }

    /**
     * Creates a petition with state "pending" on the database
     *
     * @param db
     * @param sender
     * @param receiver
     * @return
     */
    suspend fun createPetition(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            db.collection("users").document(sender).collection("petitions").document(id).set(hashMapOf(
                "id" to id,
                "sender" to sender,
                "receiver" to receiver,
                "state" to "pending"
            )).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING PETITION IN DATABASE ERROR", e)
            false
        }
    }

    /**
     * Checks if the petition is pending by the receiver
     *
     * @param receiver
     * @return
     */
    suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("receiver",receiver).whereEqualTo("state","pending").get().await()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF PETITION IS PENDING ERROR", e)
            null
        }
    }

    /**
     * Checks if the petition is pending by the sender
     *
     * @param sender
     * @return
     */
    suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("sender",sender).whereEqualTo("state","pending").get().await()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF PETITION IS PENDING ERROR", e)
            null
        }
    }
}
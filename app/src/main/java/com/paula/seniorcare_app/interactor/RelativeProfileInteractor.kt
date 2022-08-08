package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativeProfileContract
import kotlinx.coroutines.tasks.await
import java.util.*

class RelativeProfileInteractor: RelativeProfileContract.Interactor {
    override suspend fun setManagerOnDatabase(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(currentUid).collection("managers").document(relativeUid).set(hashMapOf("uid" to relativeUid)).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "SETTING MANAGER IN DATABASE ERROR", e)
            false
        }
    }

    override suspend fun deleteManagerOnDatabase(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(currentUid).collection("managers").document(relativeUid).delete().await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "DELETING MANAGER IN DATABASE ERROR", e)
            false
        }
    }

    override suspend fun deleteRelativeFromDB(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return try {
            db.collection("users").document(currentUid).collection("relatives").document(uid).delete().await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "DELETING RELATIVE ERROR", e)
            false
        }
    }

    override suspend fun createPetitionInDatabase(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
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

    override suspend fun relativeIsManager(uid: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("managers").document(uid).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS MANAGER ERROR", e)
            false
        }
    }

    override suspend fun relativeIsAdded(uid: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("relatives").document(uid).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS ADDED ERROR", e)
            false
        }
    }

    override suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("receiver",receiver).get().await()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF PETITION IS PENDING ERROR", e)
            null
        }
    }

    override suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("sender",sender).get().await()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF PETITION IS PENDING ERROR", e)
            null
        }
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING USER FROM DATABASE ERROR", e)
            null
        }
    }
}
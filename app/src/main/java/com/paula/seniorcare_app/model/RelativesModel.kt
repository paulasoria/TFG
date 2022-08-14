package com.paula.seniorcare_app.model

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class RelativesModel {
    suspend fun getAddedRelatives(db: FirebaseFirestore) : QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING ADDED RELATIVES LIST ERROR", e)
            null
        }
    }

    suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            return db.collection("users").document(relativeUid).collection("managers").document(currentUid.toString()).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS MANAGER ERROR", e)
            false
        }
    }

    suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return try {
            val data = db.collection("users").orderBy("email").startAt(query).endAt(query+"\uf8ff").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING SEARCH USERS ERROR", e)
            null
        }
    }

    suspend fun setManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(currentUid).collection("managers").document(relativeUid).set(hashMapOf("uid" to relativeUid)).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "SETTING MANAGER IN DATABASE ERROR", e)
            false
        }
    }

    suspend fun deleteManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(currentUid).collection("managers").document(relativeUid).delete().await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "DELETING MANAGER IN DATABASE ERROR", e)
            false
        }
    }

    suspend fun deleteRelative(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return try {
            db.collection("users").document(currentUid).collection("relatives").document(uid).delete().await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "DELETING RELATIVE ERROR", e)
            false
        }
    }

    suspend fun relativeIsManager(uid: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("managers").document(uid).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS MANAGER ERROR", e)
            false
        }
    }

    suspend fun relativeIsAdded(uid: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("relatives").document(uid).get().await().exists()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CONSULTING IF RELATIVE IS ADDED ERROR", e)
            false
        }
    }
}
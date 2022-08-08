package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.paula.seniorcare_app.contract.LogInContract
import kotlinx.coroutines.tasks.await

class LogInInteractor: LogInContract.Interactor {
    override suspend fun logIn(email: String, password: String): Boolean {
        return try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "LOG IN ERROR", e)
            false
        }
    }

    override suspend fun resetPassword(email: String): Boolean {
        return try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "RESETTING PASSWORD ERROR", e)
            false
        }
    }

    override suspend fun getUser(uid: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING USER FROM DATABASE ERROR", e)
            null
        }
    }

    override suspend fun changeUserToken(uid: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        return try {
            val token = FirebaseInstanceId.getInstance().instanceId.await().token
            db.collection("users").document(uid).update("token", token).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CHANGING USER TOKEN ERROR", e)
            false
        }
    }
}
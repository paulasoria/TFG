package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.AuthContract
import kotlinx.coroutines.tasks.await

class AuthInteractor: AuthContract.Interactor {
    override suspend fun getUser(uid: String): DocumentSnapshot?{
        val db = FirebaseFirestore.getInstance()
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING USER FROM DATABASE ERROR", e)
            null
        }
    }

    override suspend fun createUserFromGoogle(account: GoogleSignInAccount): Boolean {
        val db = FirebaseFirestore.getInstance()
        return try {
            val token = FirebaseInstanceId.getInstance().instanceId.await().token
            db.collection("users").document(account.id.toString()).set(
                hashMapOf(
                    "uid" to account.id.toString(),
                    "token" to token,
                    "image" to R.drawable.no_photo_user,
                    "name" to account.displayName,
                    "email" to account.email,
                    "role" to "Administrador"
                )
            ).await()
            true
        }  catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING USER FROM GOOGLE ERROR", e)
            false
        }
    }
}
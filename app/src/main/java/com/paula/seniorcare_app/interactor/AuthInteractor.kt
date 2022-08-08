package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.provider.Settings.System.getString
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
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

    override suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            val token = FirebaseInstanceId.getInstance().instanceId.await().token
            db.collection("users").document(uid).set(
                hashMapOf(
                    "uid" to uid,
                    "token" to token,
                    "image" to "https://firebasestorage.googleapis.com/v0/b/seniorcare-tfg.appspot.com/o/no_photo_user.jpg?alt=media&token=5c2a71ea-774b-450a-868c-4fce85e356c8",
                    "name" to account.displayName,
                    "email" to account.email,
                    "role" to googleRole
                )
            ).await()
            true
        }  catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING USER FROM GOOGLE ERROR", e)
            false
        }
    }
}
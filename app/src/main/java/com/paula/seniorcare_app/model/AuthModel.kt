package com.paula.seniorcare_app.model

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.tasks.await

class AuthModel {
    /**
     * Creates an account when accesing with Google Services
     *
     * @param account
     * @param googleRole
     * @return
     */
    suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean {
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

    /**
     * Logs in the user with the email and the password provided
     *
     * @param email
     * @param password
     * @return
     */
    suspend fun logIn(email: String, password: String): Boolean {
        return try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "LOG IN ERROR", e)
            false
        }
    }

    /**
     * Sends an email for reseting the password of the email provided
     *
     * @param email
     * @return
     */
    suspend fun resetPassword(email: String): Boolean {
        return try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "RESETTING PASSWORD ERROR", e)
            false
        }
    }

    /**
     * Changes the user token when he access the application on another device
     *
     * @param uid
     * @return
     */
    suspend fun changeUserToken(uid: String): Boolean {
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

    /**
     * Signs up  the user with the email and the password provided
     *
     * @param email
     * @param password
     * @return
     */
    suspend fun signUp(email: String, password: String): Boolean {
        return try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "SIGN UP ERROR", e)
            false
        }
    }
}
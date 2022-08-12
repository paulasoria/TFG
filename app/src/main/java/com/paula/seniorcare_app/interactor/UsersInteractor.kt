package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class UsersInteractor {
    suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING USER ERROR", e)
            null
        }
    }

    suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            val filepath = st.child(uid).child(filename)
            filepath.putFile(uri).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPLOADING PHOTO ERROR", e)
            false
        }
    }

    suspend fun getPhotoUrl(st: StorageReference, filename: String): String? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            val filepath = st.child(uid).child(filename)
            val result = filepath.downloadUrl.await()
            val url = result.toString()
            url
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING PHOTO URL ERROR", e)
            null
        }
    }

    suspend fun updatePhotoUrl(db: FirebaseFirestore, url: String): Boolean? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            db.collection("users").document(uid).update("image", url).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPDATING PHOTO URL ERROR", e)
            false
        }
    }

    suspend fun editUserName(db: FirebaseFirestore, name: String): Boolean? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            db.collection("users").document(uid).update("name", name).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPDATING USER NAME ERROR", e)
            false
        }
    }

    suspend fun createUser(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val token = FirebaseInstanceId.getInstance().instanceId.await().token
            db.collection("users").document(uid).set(
                hashMapOf(
                    "uid" to uid,
                    "token" to token,
                    "image" to url,
                    "name" to name,
                    "email" to email,
                    "role" to roleMenu
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING USER IN DATABASE ERROR", e)
            false
        }
    }
}
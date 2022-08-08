package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.ProfileContract
import kotlinx.coroutines.tasks.await

class ProfileInteractor: ProfileContract.Interactor {
    override suspend fun getCurrentUserFromDB(db: FirebaseFirestore): DocumentSnapshot? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING CURRENT USER ERROR", e)
            null
        }
    }

    override suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean {
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

    override suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String? {
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

    override suspend fun updatePhotoURLForUser(db: FirebaseFirestore, url: String): Boolean? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            db.collection("users").document(uid).update("image", url).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPDATING PHOTO URL ERROR", e)
            false
        }
    }

    override suspend fun editUserNameInDatabase(db: FirebaseFirestore, name: String): Boolean? {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return try {
            db.collection("users").document(uid).update("name", name).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPDATING USER NAME ERROR", e)
            false
        }
    }
}
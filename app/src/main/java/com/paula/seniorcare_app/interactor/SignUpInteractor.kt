package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.SignUpContract
import kotlinx.coroutines.tasks.await

class SignUpInteractor: SignUpContract.Interactor {
    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "SIGN UP ERROR", e)
            false
        }
    }

    override suspend fun createUserInDatabase(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean {
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

    override suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val filepath = st.child(uid).child(filename)
            filepath.putFile(uri).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPLOADING PHOTO ERROR", e)
            false
        }
    }

    override suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String? {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val filepath = st.child(uid).child(filename)
            val result = filepath.downloadUrl.await()
            val url = result.toString()
            url
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING PHOTO URL ERROR", e)
            null
        }
    }
}
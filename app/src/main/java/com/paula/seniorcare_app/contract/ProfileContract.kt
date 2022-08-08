package com.paula.seniorcare_app.contract

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

interface ProfileContract {
    interface View {
        fun showEditNameDialog()
    }

    interface Presenter {
        suspend fun getCurrentUserFromDB(db: FirebaseFirestore): DocumentSnapshot?
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String?
        suspend fun updatePhotoURLForUser(db: FirebaseFirestore, url: String): Boolean?
        suspend fun editUserNameInDatabase(db: FirebaseFirestore, name: String): Boolean?
    }

    interface Interactor {
        suspend fun getCurrentUserFromDB(db: FirebaseFirestore): DocumentSnapshot?
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String?
        suspend fun updatePhotoURLForUser(db: FirebaseFirestore, url: String): Boolean?
        suspend fun editUserNameInDatabase(db: FirebaseFirestore, name: String): Boolean?
    }
}
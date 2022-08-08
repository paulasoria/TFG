package com.paula.seniorcare_app.contract

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class ProfileTvContract {
    interface View {
        fun selectImageFromGallery()
        fun deleteUserData()
        fun showEditNameDialog()
    }

    interface Presenter {
        suspend fun getUserFromDB(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun editUserNameInDatabase(db: FirebaseFirestore, uid: String, name: String)
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String, uid: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String, uid: String): String?
        suspend fun updatePhotoURLForUser(db: FirebaseFirestore, uid: String, url: String): Boolean?
    }

    interface Interactor {
        suspend fun getUserFromDB(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun editUserNameInDatabase(db: FirebaseFirestore, uid: String, name: String)
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String, uid: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String, uid: String): String?
        suspend fun updatePhotoURLForUser(db: FirebaseFirestore, uid: String, url: String): Boolean?
    }
}
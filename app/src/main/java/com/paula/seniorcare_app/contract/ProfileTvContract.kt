package com.paula.seniorcare_app.contract

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

interface ProfileTvContract {
    interface View {
        fun selectImageFromGallery()
        fun deleteUserData()
        fun showEditNameDialog()
    }

    interface Presenter {
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun editUserName(db: FirebaseFirestore, name: String): Boolean?
        suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean
        suspend fun getPhotoUrl(st: StorageReference, filename: String): String?
        suspend fun updatePhotoUrl(db: FirebaseFirestore, url: String): Boolean?
    }
}
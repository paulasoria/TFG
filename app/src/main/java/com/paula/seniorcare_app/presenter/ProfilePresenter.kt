package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.ProfileContract
import com.paula.seniorcare_app.model.UsersModel

class ProfilePresenter : ProfileContract.Presenter {
    private val usersModel = UsersModel()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }

    override suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean {
        return usersModel.uploadPhoto(st, uri, filename)
    }

    override suspend fun getPhotoUrl(st: StorageReference, filename: String): String? {
        return usersModel.getPhotoUrl(st, filename)
    }

    override suspend fun updatePhotoUrl(db: FirebaseFirestore, url: String): Boolean? {
        return usersModel.updatePhotoUrl(db, url)
    }

    override suspend fun editUserName(db: FirebaseFirestore, name: String): Boolean? {
        return usersModel.editUserName(db, name)
    }
}
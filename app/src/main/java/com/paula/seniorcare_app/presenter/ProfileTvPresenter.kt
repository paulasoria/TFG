package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.ProfileTvContract
import com.paula.seniorcare_app.interactor.UsersInteractor

class ProfileTvPresenter : ProfileTvContract.Presenter {
    private val usersInteractor = UsersInteractor()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean {
        return usersInteractor.uploadPhoto(st, uri, filename)
    }

    override suspend fun getPhotoUrl(st: StorageReference, filename: String): String? {
        return usersInteractor.getPhotoUrl(st, filename)
    }

    override suspend fun updatePhotoUrl(db: FirebaseFirestore, url: String): Boolean? {
        return usersInteractor.updatePhotoUrl(db, url)
    }

    override suspend fun editUserName(db: FirebaseFirestore, name: String): Boolean? {
        return usersInteractor.editUserName(db, name)
    }
}
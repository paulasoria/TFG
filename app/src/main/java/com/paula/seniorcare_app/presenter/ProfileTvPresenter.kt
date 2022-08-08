package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.ProfileTvContract

class ProfileTvPresenter(private val profileTvView: ProfileTvContract.View, private val profileTvInteractor: ProfileTvContract.Interactor) : ProfileTvContract.Presenter {
    override suspend fun getUserFromDB(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return profileTvInteractor.getUserFromDB(db, uid)
    }

    override suspend fun editUserNameInDatabase(db: FirebaseFirestore, uid: String, name: String) {
        profileTvInteractor.editUserNameInDatabase(db, uid, name)
    }

    override suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String, uid: String): Boolean {
        return profileTvInteractor.uploadPhotoToFireStorage(st, uri, filename, uid)
    }

    override suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String, uid: String): String? {
        return profileTvInteractor.getURLofPhotoInFireStorage(st, filename, uid)
    }

    override suspend fun updatePhotoURLForUser(db: FirebaseFirestore, uid: String, url: String): Boolean? {
        return profileTvInteractor.updatePhotoURLForUser(db, uid, url)
    }
}
package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.ProfileContract

class ProfilePresenter(private val profileView: ProfileContract.View, private val profileInteractor: ProfileContract.Interactor) : ProfileContract.Presenter {
    override suspend fun getCurrentUserFromDB(db: FirebaseFirestore): DocumentSnapshot? {
        return profileInteractor.getCurrentUserFromDB(db)
    }

    override suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean {
        return profileInteractor.uploadPhotoToFireStorage(st, uri, filename)
    }

    override suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String? {
        return profileInteractor.getURLofPhotoInFireStorage(st, filename)
    }

    override suspend fun updatePhotoURLForUser(db: FirebaseFirestore, url: String): Boolean? {
        return profileInteractor.updatePhotoURLForUser(db, url)
    }

    override suspend fun editUserNameInDatabase(db: FirebaseFirestore, name: String): Boolean? {
        return profileInteractor.editUserNameInDatabase(db, name)
    }
}
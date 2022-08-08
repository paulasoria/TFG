package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.SignUpContract

class SignUpPresenter (private val signUpView: SignUpContract.View, private val signUpInteractor: SignUpContract.Interactor) : SignUpContract.Presenter {
    override suspend fun signUp(email: String, password: String): Boolean {
        return signUpInteractor.signUp(email, password)
    }

    override suspend fun createUserInDatabase(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean {
        return signUpInteractor.createUserInDatabase(db, url, name, email, roleMenu)
    }

    override suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean {
        return signUpInteractor.uploadPhotoToFireStorage(st, uri, filename)
    }

    override suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String? {
        return signUpInteractor.getURLofPhotoInFireStorage(st, filename)
    }
}
package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.SignUpContract
import com.paula.seniorcare_app.interactor.AuthInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class SignUpPresenter : SignUpContract.Presenter {
    private val authInteractor = AuthInteractor()
    private val userInteractor = UsersInteractor()

    override suspend fun signUp(email: String, password: String): Boolean {
        return authInteractor.signUp(email, password)
    }

    override suspend fun createUser(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean {
        return userInteractor.createUser(db, url, name, email, roleMenu)
    }

    override suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean {
        return userInteractor.uploadPhoto(st, uri, filename)
    }

    override suspend fun getPhotoUrl(st: StorageReference, filename: String): String? {
        return userInteractor.getPhotoUrl(st, filename)
    }
}
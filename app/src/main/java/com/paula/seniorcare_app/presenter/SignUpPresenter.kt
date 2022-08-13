package com.paula.seniorcare_app.presenter

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.contract.SignUpContract
import com.paula.seniorcare_app.model.AuthModel
import com.paula.seniorcare_app.model.UsersModel

class SignUpPresenter : SignUpContract.Presenter {
    private val authModel = AuthModel()
    private val userModel = UsersModel()

    override suspend fun signUp(email: String, password: String): Boolean {
        return authModel.signUp(email, password)
    }

    override suspend fun createUser(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean {
        return userModel.createUser(db, url, name, email, roleMenu)
    }

    override suspend fun uploadPhoto(st: StorageReference, uri: Uri, filename: String): Boolean {
        return userModel.uploadPhoto(st, uri, filename)
    }

    override suspend fun getPhotoUrl(st: StorageReference, filename: String): String? {
        return userModel.getPhotoUrl(st, filename)
    }
}
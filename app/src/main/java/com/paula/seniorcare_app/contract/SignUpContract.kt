package com.paula.seniorcare_app.contract

import android.net.Uri
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

interface SignUpContract {
    interface View {
        fun emptyEditText(x: TextInputLayout)
        fun showAlertSignUp()
    }

    interface Presenter {
        suspend fun signUp(email: String, password: String): Boolean
        suspend fun createUserInDatabase(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String?
    }

    interface Interactor {
        suspend fun signUp(email: String, password: String): Boolean
        suspend fun createUserInDatabase(db: FirebaseFirestore, url: String, name: String, email: String, roleMenu: String): Boolean
        suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String): Boolean
        suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String): String?
    }
}
package com.paula.seniorcare_app.contract

import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

interface LogInContract {
    interface View {
        fun emptyEditText(text: TextInputLayout)
        fun showAlertLogIn()
        fun showResetPasswordDialog()
    }

    interface Presenter {
        suspend fun logIn(email: String, password: String): Boolean
        suspend fun changeUserToken(uid: String)
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun resetPassword(email: String): Boolean
    }
}
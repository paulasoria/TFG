package com.paula.seniorcare_app.contract

import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentSnapshot

interface LogInContract {
    interface View {
        fun emptyEditText(text: TextInputLayout)
        fun showAlertLogIn()
        fun successfullySentEmail(success: Boolean)
    }

    interface Presenter {
        suspend fun logIn(email: String, password: String): Boolean
        suspend fun changeUserToken(uid: String)
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun resetPassword(email: String): Boolean
        fun succesfullySentEmail(success: Boolean)
    }

    interface Interactor {
        suspend fun logIn(email: String, password: String): Boolean
        suspend fun resetPassword(email: String): Boolean
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun changeUserToken(uid: String): Boolean
    }
}
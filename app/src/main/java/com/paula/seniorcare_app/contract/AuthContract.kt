package com.paula.seniorcare_app.contract

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot

interface AuthContract {
    interface View {
        fun getRotation(context: Context): String
        fun showAlertGoogle()
    }

    interface Presenter {
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun createUserFromGoogle(account: GoogleSignInAccount): Boolean
    }

    interface Interactor {
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun createUserFromGoogle(account: GoogleSignInAccount): Boolean
    }
}
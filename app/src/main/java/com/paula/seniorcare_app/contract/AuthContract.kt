package com.paula.seniorcare_app.contract

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot

interface AuthContract {
    interface View {
        fun loadSession()
        fun showAlertGoogle()
        fun showChooseRoleDialog(account: GoogleSignInAccount)
    }

    interface Presenter {
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean
    }

    interface Interactor {
        suspend fun getUser(uid: String): DocumentSnapshot?
        suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean
    }
}
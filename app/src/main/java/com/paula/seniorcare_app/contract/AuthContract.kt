package com.paula.seniorcare_app.contract

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

interface AuthContract {
    interface View {
        fun loadSession()
        fun showAlertGoogle()
        fun showChooseRoleDialog(account: GoogleSignInAccount)
    }

    interface Presenter {
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean
    }
}
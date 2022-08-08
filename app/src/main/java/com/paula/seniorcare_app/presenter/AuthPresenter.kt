package com.paula.seniorcare_app.presenter

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot
import com.paula.seniorcare_app.contract.AuthContract

class AuthPresenter(private val authView: AuthContract.View, private val authInteractor: AuthContract.Interactor) : AuthContract.Presenter {
    override suspend fun getUser(uid: String): DocumentSnapshot? {
        return authInteractor.getUser(uid)
    }

    override suspend fun createUserFromGoogle(account: GoogleSignInAccount): Boolean {
        return authInteractor.createUserFromGoogle(account)
    }
}
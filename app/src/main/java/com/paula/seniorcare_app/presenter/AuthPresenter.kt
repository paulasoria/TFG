package com.paula.seniorcare_app.presenter

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.AuthContract
import com.paula.seniorcare_app.interactor.AuthInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class AuthPresenter : AuthContract.Presenter {
    private val usersInteractor = UsersInteractor()
    private val authInteractor = AuthInteractor()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean {
        return authInteractor.createUserFromGoogle(account, googleRole)
    }
}
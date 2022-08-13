package com.paula.seniorcare_app.presenter

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.AuthContract
import com.paula.seniorcare_app.model.AuthModel
import com.paula.seniorcare_app.model.UsersModel

class AuthPresenter : AuthContract.Presenter {
    private val usersModel = UsersModel()
    private val authModel = AuthModel()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }

    override suspend fun createUserFromGoogle(account: GoogleSignInAccount, googleRole: String): Boolean {
        return authModel.createUserFromGoogle(account, googleRole)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.LogInContract
import com.paula.seniorcare_app.model.AuthModel
import com.paula.seniorcare_app.model.UsersModel

class LogInPresenter : LogInContract.Presenter {
    private val authModel = AuthModel()
    private val usersModel = UsersModel()

    override suspend fun logIn(email: String, password: String): Boolean {
        return authModel.logIn(email, password)
    }

    override suspend fun changeUserToken(uid: String) {
        authModel.changeUserToken(uid)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return authModel.resetPassword(email)
    }
}
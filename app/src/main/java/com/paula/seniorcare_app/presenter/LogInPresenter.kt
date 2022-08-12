package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.LogInContract
import com.paula.seniorcare_app.interactor.AuthInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class LogInPresenter : LogInContract.Presenter {
    private val authInteractor = AuthInteractor()
    private val usersInteractor = UsersInteractor()

    override suspend fun logIn(email: String, password: String): Boolean {
        return authInteractor.logIn(email, password)
    }

    override suspend fun changeUserToken(uid: String) {
        authInteractor.changeUserToken(uid)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return authInteractor.resetPassword(email)
    }
}
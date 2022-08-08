package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.paula.seniorcare_app.contract.LogInContract

class LogInPresenter(private val logInView: LogInContract.View, private val logInInteractor: LogInContract.Interactor) : LogInContract.Presenter {
    override suspend fun logIn(email: String, password: String): Boolean {
        return logInInteractor.logIn(email, password)
    }

    override suspend fun changeUserToken(uid: String) {
        logInInteractor.changeUserToken(uid)
    }

    override suspend fun getUser(uid: String): DocumentSnapshot? {
        return logInInteractor.getUser(uid)
    }

    override suspend fun resetPassword(email: String): Boolean {
        return logInInteractor.resetPassword(email)
    }

    override fun succesfullySentEmail(success: Boolean){
        logInView.successfullySentEmail(success)
    }
}
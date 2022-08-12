package com.paula.seniorcare_app.presenter

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.IncomingVideocallContract
import com.paula.seniorcare_app.interactor.UsersInteractor
import com.paula.seniorcare_app.interactor.VideocallsInteractor

class IncomingVideocallPresenter : IncomingVideocallContract.Presenter {
    private val videocallsInteractor = VideocallsInteractor()
    private val usersInteractor = UsersInteractor()

    override fun rejectCallHttp(token: String, callId: String): Task<String> {
        return videocallsInteractor.rejectCallHttp(token, callId)
    }

    override fun acceptCallHttp(token: String, callId: String): Task<String> {
        return videocallsInteractor.acceptCallHttp(token, callId)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return videocallsInteractor.changeStateCall(db, callId, state)
    }
}
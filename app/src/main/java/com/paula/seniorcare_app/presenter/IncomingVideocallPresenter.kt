package com.paula.seniorcare_app.presenter

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.IncomingVideocallContract

class IncomingVideocallPresenter(private val incomingVideocallView: IncomingVideocallContract.View, private val incomingVideocallInteractor: IncomingVideocallContract.Interactor) : IncomingVideocallContract.Presenter {
    override fun rejectCallHttp(token: String, callId: String): Task<String> {
        return incomingVideocallInteractor.rejectCallHttp(token, callId)
    }

    override fun acceptCallHttp(token: String, callId: String): Task<String> {
        return incomingVideocallInteractor.acceptCallHttp(token, callId)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return incomingVideocallInteractor.getUser(db, uid)
    }

    override suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return incomingVideocallInteractor.changeStateCall(db, callId, state)
    }
}
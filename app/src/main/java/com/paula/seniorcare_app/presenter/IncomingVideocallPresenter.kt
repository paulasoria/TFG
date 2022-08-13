package com.paula.seniorcare_app.presenter

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.IncomingVideocallContract
import com.paula.seniorcare_app.model.UsersModel
import com.paula.seniorcare_app.model.VideocallsModel

class IncomingVideocallPresenter : IncomingVideocallContract.Presenter {
    private val videocallsModel = VideocallsModel()
    private val usersModel = UsersModel()

    override fun rejectCallHttp(token: String, callId: String): Task<String> {
        return videocallsModel.rejectCallHttp(token, callId)
    }

    override fun acceptCallHttp(token: String, callId: String): Task<String> {
        return videocallsModel.acceptCallHttp(token, callId)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }

    override suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return videocallsModel.changeStateCall(db, callId, state)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.contract.OutgoingVideocallContract
import com.paula.seniorcare_app.interactor.UsersInteractor
import com.paula.seniorcare_app.interactor.VideocallsInteractor

class OutgoingVideocallPresenter : OutgoingVideocallContract.Presenter {
    private val videocallsInteractor = VideocallsInteractor()
    private val usersInteractor = UsersInteractor()

    override fun rejectCallHttp(token: String, callId: String): Task<String> {
        return videocallsInteractor.rejectCallHttp(token, callId)
    }

    override fun createCallHttp(senderUid: String, senderName: String, senderEmail: String, senderImage: String, receiverUid: String, receiverName: String, receiverEmail: String, receiverImage: String, receiverToken: String, callId: String): Task<String> {
        return videocallsInteractor.createCallHttp(senderUid, senderName, senderEmail, senderImage, receiverUid, receiverName, receiverEmail, receiverImage, receiverToken, callId)
    }

    override suspend fun createCall(db: FirebaseFirestore, receiver: String, receiverName: String, senderName: String?, date: String?, time: String?, state: String, timestamp: FieldValue): String? {
        return videocallsInteractor.createCall(db, receiver, receiverName, senderName, date, time, state, timestamp)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean {
        return videocallsInteractor.changeStateCall(db,callId, state)
    }
}
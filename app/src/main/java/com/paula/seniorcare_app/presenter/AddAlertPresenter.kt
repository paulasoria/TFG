package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddAlertContract
import java.util.HashMap

class AddAlertPresenter(private val addAlertView: AddAlertContract.View, private val addAlertInteractor: AddAlertContract.Interactor) : AddAlertContract.Presenter {
    override suspend fun getAddedRelativesList(db: FirebaseFirestore) : QuerySnapshot? {
        return addAlertInteractor.getAddedRelativesList(db)
    }

    override suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean {
        return addAlertInteractor.isManagerOfRelative(db, relativeUid)
    }

    override suspend fun getReceiverOfAlert(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return addAlertInteractor.getReceiverOfAlert(db, email)
    }

    override suspend fun createAlertInDatabase(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return addAlertInteractor.createAlertInDatabase(db, receiver, tag, repetition, time, daysOfWeek, date)
    }

    override suspend fun updateAlertInDatabase(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return addAlertInteractor.updateAlertInDatabase(db, id, receiver, tag, repetition, time, daysOfWeek, date)
    }
}
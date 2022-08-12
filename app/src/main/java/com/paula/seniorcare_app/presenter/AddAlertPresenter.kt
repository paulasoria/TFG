package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddAlertContract
import com.paula.seniorcare_app.interactor.AlertsInteractor
import com.paula.seniorcare_app.interactor.RelativesInteractor
import java.util.HashMap

class AddAlertPresenter : AddAlertContract.Presenter {
    private val relativesInteractor = RelativesInteractor()
    private val alertsInteractor = AlertsInteractor()

    override suspend fun getAddedRelatives(db: FirebaseFirestore) : QuerySnapshot? {
        return relativesInteractor.getAddedRelatives(db)
    }

    override suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesInteractor.isManagerOfRelative(db, relativeUid)
    }

    override suspend fun getReceiverOfAlertEmail(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return alertsInteractor.getReceiverOfAlertEmail(db, email)
    }

    override suspend fun createAlert(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return alertsInteractor.createAlert(db, receiver, tag, repetition, time, daysOfWeek, date)
    }

    override suspend fun updateAlert(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return alertsInteractor.updateAlert(db, id, receiver, tag, repetition, time, daysOfWeek, date)
    }
}
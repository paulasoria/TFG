package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddAlertContract
import com.paula.seniorcare_app.model.AlertsModel
import com.paula.seniorcare_app.model.RelativesModel
import java.util.HashMap

class AddAlertPresenter : AddAlertContract.Presenter {
    private val relativesModel = RelativesModel()
    private val alertsModel = AlertsModel()

    override suspend fun getAddedRelatives(db: FirebaseFirestore) : QuerySnapshot? {
        return relativesModel.getAddedRelatives(db)
    }

    override suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesModel.isManagerOfRelative(db, relativeUid)
    }

    override suspend fun getReceiverOfAlertEmail(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return alertsModel.getReceiverOfAlertEmail(db, email)
    }

    override suspend fun createAlert(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return alertsModel.createAlert(db, receiver, tag, repetition, time, daysOfWeek, date)
    }

    override suspend fun updateAlert(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean {
        return alertsModel.updateAlert(db, id, receiver, tag, repetition, time, daysOfWeek, date)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AlertsContract
import com.paula.seniorcare_app.model.AlertsModel

class AlertsPresenter : AlertsContract.Presenter {
    private val alertsModel = AlertsModel()

    override suspend fun getReceiverOfAlertUid(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return alertsModel.getReceiverOfAlertUid(db, uid)
    }

    override suspend fun getConfiguredAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return alertsModel.getConfiguredAlerts(db)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AlertsContract
import com.paula.seniorcare_app.interactor.AlertsInteractor

class AlertsPresenter : AlertsContract.Presenter {
    private val alertsInteractor = AlertsInteractor()

    override suspend fun getReceiverOfAlertUid(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return alertsInteractor.getReceiverOfAlertUid(db, uid)
    }

    override suspend fun getConfiguredAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return alertsInteractor.getConfiguredAlerts(db)
    }
}
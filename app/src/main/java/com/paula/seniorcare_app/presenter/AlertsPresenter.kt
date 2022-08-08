package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AlertsContract

class AlertsPresenter(private val alertsView: AlertsContract.View, private val alertsInteractor: AlertsContract.Interactor) : AlertsContract.Presenter {
    override suspend fun getReceiverOfAlert(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return alertsInteractor.getReceiverOfAlert(db, uid)
    }

    override suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore): QuerySnapshot? {
        return alertsInteractor.getConfiguredAlertsFromDB(db)
    }
}
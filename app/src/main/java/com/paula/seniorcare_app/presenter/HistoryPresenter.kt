package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.HistoryContract
import com.paula.seniorcare_app.model.AlertsModel
import com.paula.seniorcare_app.model.VideocallsModel

class HistoryPresenter : HistoryContract.Presenter {
    private val videocallsModel = VideocallsModel()
    private val alertsModel = AlertsModel()

    override suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return videocallsModel.getVideocalls(db)
    }

    override suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return alertsModel.getHistoryOfAlerts(db)
    }
}
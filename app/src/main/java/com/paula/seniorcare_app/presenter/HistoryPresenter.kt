package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.HistoryContract
import com.paula.seniorcare_app.interactor.AlertsInteractor
import com.paula.seniorcare_app.interactor.VideocallsInteractor

class HistoryPresenter : HistoryContract.Presenter {
    private val videocallsInteractor = VideocallsInteractor()
    private val alertsInteractor = AlertsInteractor()

    override suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return videocallsInteractor.getVideocalls(db)
    }

    override suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return alertsInteractor.getHistoryOfAlerts(db)
    }
}
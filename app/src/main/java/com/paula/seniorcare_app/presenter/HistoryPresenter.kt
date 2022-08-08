package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.HistoryContract

class HistoryPresenter(private val historyView: HistoryContract.View, private val historyInteractor: HistoryContract.Interactor) : HistoryContract.Presenter {
    override suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return historyInteractor.getVideocalls(db)
    }

    override suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return historyInteractor.getHistoryOfAlerts(db)
    }
}
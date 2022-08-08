package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.History
import com.paula.seniorcare_app.dataclass.Videocall
import java.util.ArrayList

interface HistoryContract {
    interface View {
        fun setHistoryOfVideocalls()
        fun setHistoryOfAlerts()
        fun showResultsVideocalls(videocallList: ArrayList<Videocall>)
        fun showResultsAlerts(historyList: ArrayList<History>)
    }

    interface Presenter {
        suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot?
        suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot?
    }

    interface Interactor {
        suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot?
        suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot?
    }
}
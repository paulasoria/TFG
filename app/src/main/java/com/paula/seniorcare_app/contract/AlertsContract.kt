package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.Alert

interface AlertsContract {
    interface View {
        fun showResults(alertsList: ArrayList<Alert>)
    }

    interface Presenter {
        suspend fun getReceiverOfAlert(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore): QuerySnapshot?
    }

    interface Interactor {
        suspend fun getReceiverOfAlert(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore): QuerySnapshot?
    }
}
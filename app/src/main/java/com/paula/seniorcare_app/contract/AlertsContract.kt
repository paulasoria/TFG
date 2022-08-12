package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.Alert

interface AlertsContract {
    interface View {
        fun showConfiguredAlerts()
        fun showResults(alertsList: ArrayList<Alert>)
    }

    interface Presenter {
        suspend fun getReceiverOfAlertUid(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getConfiguredAlerts(db: FirebaseFirestore): QuerySnapshot?
    }
}
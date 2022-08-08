package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AlertsContract
import kotlinx.coroutines.tasks.await

class AlertsInteractor: AlertsContract.Interactor {
    override suspend fun getReceiverOfAlert(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try{
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING RECEIVER OF ALERT ERROR", e)
            null
        }
    }

    override suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            //val data = db.collection("users").document(uid).collection("alerts").get().await()
            val data = db.collection("users").document(currentUid.toString())
                .collection("alerts").whereEqualTo("sender",currentUid.toString()).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING CONFIGURED ALERTS ERROR", e)
            null
        }
    }
}
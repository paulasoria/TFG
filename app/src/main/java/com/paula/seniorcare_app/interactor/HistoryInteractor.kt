package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.HistoryContract
import kotlinx.coroutines.tasks.await

class HistoryInteractor: HistoryContract.Interactor {
    override suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val data = db.collection("videocalls").orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF VIDEOCALLS ERROR", e)
            null
        }
    }

    override suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("historyAlerts").orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF ALERTS ERROR", e)
            null
        }
    }
}
package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.PetitionsContract
import kotlinx.coroutines.tasks.await

class PetitionsInteractor: PetitionsContract.Interactor {
    override suspend fun getPendingPetitionsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return try {
            val data = db.collection("users").document(uid).collection("petitions").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING PENDING PETITIONS ERROR", e)
            null
        }
    }

    override suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try{
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING SENDER OF PETITION ERROR", e)
            null
        }
    }
}
package com.paula.seniorcare_app.interactor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativesContract
import kotlinx.coroutines.tasks.await

class RelativesInteractor: RelativesContract.Interactor {
    override suspend fun getAddedRelativesList(db: FirebaseFirestore) : QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            null
        }
    }
}
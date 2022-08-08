package com.paula.seniorcare_app.interactor

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddRelativeContract
import kotlinx.coroutines.tasks.await

class AddRelativeInteractor: AddRelativeContract.Interactor {
    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING USER ERROR", e)
            null
        }
    }

    override suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return try {
            val data = db.collection("users").orderBy("email").startAt(query).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING SEARCH USERS ERROR", e)
            null
        }
    }
}
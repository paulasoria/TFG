package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

interface RelativeProfileContract {
    interface View {
        fun sendPetition(relativeUid: String)
        fun showDeleteRelativeDialog(relativeUid: String)
    }

    interface Presenter {
        suspend fun setManager(db: FirebaseFirestore, relativeUid: String): Boolean
        suspend fun deleteManager(db: FirebaseFirestore, relativeUid: String): Boolean
        suspend fun deleteRelative(db: FirebaseFirestore, currentUid: String, uid: String): Boolean
        suspend fun createPetition(db: FirebaseFirestore, sender: String, receiver: String): Boolean
        suspend fun relativeIsManager(uid: String): Boolean
        suspend fun relativeIsAdded(uid: String): Boolean
        suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot?
        suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot?
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
    }
}
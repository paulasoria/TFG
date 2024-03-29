package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.User

interface RelativesContract {
    interface View {
        fun showAddedRelatives()
        fun showPetitions()
        fun searchRelative()
        fun showResults(addedRelativesList: ArrayList<User>)
    }

    interface Presenter {
        suspend fun getAddedRelatives(db: FirebaseFirestore) : QuerySnapshot?
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
    }
}
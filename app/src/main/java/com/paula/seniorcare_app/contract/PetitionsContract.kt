package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.Petition

interface PetitionsContract {
    interface View {
        fun getPendingPetitions()
        fun showResults(petitionsList: ArrayList<Petition>)
    }

    interface Presenter {
        suspend fun getPendingPetitionsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot?
        suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot?
    }

    interface Interactor {
        suspend fun getPendingPetitionsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot?
        suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot?
    }
}
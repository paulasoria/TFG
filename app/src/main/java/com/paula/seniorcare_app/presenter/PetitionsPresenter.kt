package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.PetitionsContract
import com.paula.seniorcare_app.model.PetitionsModel

class PetitionsPresenter : PetitionsContract.Presenter {
    private val petitionsModel = PetitionsModel()

    override suspend fun getPendingPetitions(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return petitionsModel.getPendingPetitions(db, uid)
    }

    override suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return petitionsModel.getSenderOfPetition(db, uid)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.PetitionsContract

class PetitionsPresenter(private val petitionsView: PetitionsContract.View, private val petitionsInteractor: PetitionsContract.Interactor) : PetitionsContract.Presenter {
    override suspend fun getPendingPetitionsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return petitionsInteractor.getPendingPetitionsFromDB(db, uid)
    }

    override suspend fun getSenderOfPetition(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return petitionsInteractor.getSenderOfPetition(db, uid)
    }
}
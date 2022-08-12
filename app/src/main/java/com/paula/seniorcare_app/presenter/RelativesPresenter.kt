package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativesContract
import com.paula.seniorcare_app.interactor.RelativesInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class RelativesPresenter : RelativesContract.Presenter {
    private val relativesInteractor = RelativesInteractor()
    private val usersInteractor = UsersInteractor()

    override suspend fun getAddedRelatives(db: FirebaseFirestore): QuerySnapshot? {
        return relativesInteractor.getAddedRelatives(db)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }
}
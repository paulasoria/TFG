package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativesContract

class RelativesPresenter(private val relativesView: RelativesContract.View, private val relativesInteractor: RelativesContract.Interactor) : RelativesContract.Presenter {
    override suspend fun getAddedRelativesList(db: FirebaseFirestore): QuerySnapshot? {
        return relativesInteractor.getAddedRelativesList(db)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return relativesInteractor.getUser(db, uid)
    }
}
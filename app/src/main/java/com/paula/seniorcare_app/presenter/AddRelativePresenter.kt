package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddRelativeContract

class AddRelativePresenter(private val addRelativeView: AddRelativeContract.View, private val addRelativeInteractor: AddRelativeContract.Interactor) : AddRelativeContract.Presenter {
    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return addRelativeInteractor.getUser(db, uid)
    }

    override suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return addRelativeInteractor.getSearchUsers(db, query)
    }
}
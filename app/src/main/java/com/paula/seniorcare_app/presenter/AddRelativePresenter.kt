package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddRelativeContract
import com.paula.seniorcare_app.interactor.RelativesInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class AddRelativePresenter : AddRelativeContract.Presenter {
    private val usersInteractor = UsersInteractor()
    private val relativesInteractor = RelativesInteractor()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }

    override suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return relativesInteractor.getSearchUsers(db, query)
    }
}
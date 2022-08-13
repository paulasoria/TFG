package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.AddRelativeContract
import com.paula.seniorcare_app.model.RelativesModel
import com.paula.seniorcare_app.model.UsersModel

class AddRelativePresenter : AddRelativeContract.Presenter {
    private val usersModel = UsersModel()
    private val relativesModel = RelativesModel()

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }

    override suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return relativesModel.getSearchUsers(db, query)
    }
}
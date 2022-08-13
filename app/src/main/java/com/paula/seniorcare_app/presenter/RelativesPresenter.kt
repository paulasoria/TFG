package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativesContract
import com.paula.seniorcare_app.model.RelativesModel
import com.paula.seniorcare_app.model.UsersModel

class RelativesPresenter : RelativesContract.Presenter {
    private val relativesModel = RelativesModel()
    private val usersModel = UsersModel()

    override suspend fun getAddedRelatives(db: FirebaseFirestore): QuerySnapshot? {
        return relativesModel.getAddedRelatives(db)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }
}
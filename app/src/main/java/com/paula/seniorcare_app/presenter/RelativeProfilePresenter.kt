package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativeProfileContract
import com.paula.seniorcare_app.model.PetitionsModel
import com.paula.seniorcare_app.model.RelativesModel
import com.paula.seniorcare_app.model.UsersModel

class RelativeProfilePresenter : RelativeProfileContract.Presenter {
    private val relativesModel = RelativesModel()
    private val petitionsModel = PetitionsModel()
    private val usersModel = UsersModel()

    override suspend fun setManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesModel.setManager(db, relativeUid)
    }

    override suspend fun deleteManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesModel.deleteManager(db, relativeUid)
    }

    override suspend fun deleteRelative(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return relativesModel.deleteRelative(db, currentUid, uid)
    }

    override suspend fun createPetition(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
        return petitionsModel.createPetition(db, sender, receiver)
    }

    override suspend fun relativeIsManager(uid: String): Boolean {
        return relativesModel.relativeIsManager(uid)
    }

    override suspend fun relativeIsAdded(uid: String): Boolean {
        return relativesModel.relativeIsAdded(uid)
    }

    override suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return petitionsModel.petitionIsPendingByReceiver(receiver)
    }

    override suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return petitionsModel.petitionIsPendingBySender(sender)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersModel.getUser(db, uid)
    }
}
package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativeProfileContract
import com.paula.seniorcare_app.interactor.PetitionsInteractor
import com.paula.seniorcare_app.interactor.RelativesInteractor
import com.paula.seniorcare_app.interactor.UsersInteractor

class RelativeProfilePresenter : RelativeProfileContract.Presenter {
    private val relativesInteractor = RelativesInteractor()
    private val petitionsInteractor = PetitionsInteractor()
    private val usersInteractor = UsersInteractor()

    override suspend fun setManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesInteractor.setManager(db, relativeUid)
    }

    override suspend fun deleteManager(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativesInteractor.deleteManager(db, relativeUid)
    }

    override suspend fun deleteRelative(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return relativesInteractor.deleteRelative(db, currentUid, uid)
    }

    override suspend fun createPetition(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
        return petitionsInteractor.createPetition(db, sender, receiver)
    }

    override suspend fun relativeIsManager(uid: String): Boolean {
        return relativesInteractor.relativeIsManager(uid)
    }

    override suspend fun relativeIsAdded(uid: String): Boolean {
        return relativesInteractor.relativeIsAdded(uid)
    }

    override suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return petitionsInteractor.petitionIsPendingByReceiver(receiver)
    }

    override suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return petitionsInteractor.petitionIsPendingBySender(sender)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return usersInteractor.getUser(db, uid)
    }
}
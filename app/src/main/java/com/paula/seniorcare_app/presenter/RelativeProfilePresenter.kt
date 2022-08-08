package com.paula.seniorcare_app.presenter

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.contract.RelativeProfileContract

class RelativeProfilePresenter(private val relativeProfileView: RelativeProfileContract.View, private val relativeProfileInteractor: RelativeProfileContract.Interactor): RelativeProfileContract.Presenter {
    override suspend fun setManagerOnDatabase(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativeProfileInteractor.setManagerOnDatabase(db, relativeUid)
    }

    override suspend fun deleteManagerOnDatabase(db: FirebaseFirestore, relativeUid: String): Boolean {
        return relativeProfileInteractor.deleteManagerOnDatabase(db, relativeUid)
    }

    override suspend fun deleteRelativeFromDB(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return relativeProfileInteractor.deleteRelativeFromDB(db, currentUid, uid)
    }

    override suspend fun createPetitionInDatabase(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
        return relativeProfileInteractor.createPetitionInDatabase(db, sender, receiver)
    }

    override suspend fun relativeIsManager(uid: String): Boolean {
        return relativeProfileInteractor.relativeIsManager(uid)
    }

    override suspend fun relativeIsAdded(uid: String): Boolean {
        return relativeProfileInteractor.relativeIsAdded(uid)
    }

    override suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return relativeProfileInteractor.petitionIsPendingByReceiver(receiver)
    }

    override suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return relativeProfileInteractor.petitionIsPendingBySender(sender)
    }

    override suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return relativeProfileInteractor.getUser(db, uid)
    }
}
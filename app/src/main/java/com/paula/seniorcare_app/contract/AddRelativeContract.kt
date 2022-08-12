package com.paula.seniorcare_app.contract

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.dataclass.User

interface AddRelativeContract {
    interface View {
        fun showPetitions()
        fun showResults(searchList: ArrayList<User>)
    }

    interface Presenter {
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot?
    }

    interface Interactor {
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot?
    }
}
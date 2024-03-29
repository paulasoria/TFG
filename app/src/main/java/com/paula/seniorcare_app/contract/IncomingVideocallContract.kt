package com.paula.seniorcare_app.contract

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface IncomingVideocallContract {
    interface View {
        fun rejectVideocall(db: FirebaseFirestore, callId: String, senderUid: String)
        fun acceptVideocall(db: FirebaseFirestore, callId: String, senderUid: String, receiverJwt: String)
    }

    interface Presenter {
        fun rejectCallHttp(token: String, callId: String): Task<String>
        fun acceptCallHttp(token: String, callId: String): Task<String>
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean
    }
}
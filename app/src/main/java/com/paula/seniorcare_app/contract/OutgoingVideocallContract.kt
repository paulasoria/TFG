package com.paula.seniorcare_app.contract

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

interface OutgoingVideocallContract {
    interface View {
        fun endVideocall(db: FirebaseFirestore, callId: String, receiverUid: String)
    }

    interface Presenter {
        fun rejectCallHttp(token: String, callId: String): Task<String>
        fun createCallHttp(senderUid: String, senderName: String, senderEmail: String, senderImage: String, receiverUid: String, receiverName: String, receiverEmail: String, receiverImage: String, receiverToken: String, callId: String): Task<String>
        suspend fun createCall(db: FirebaseFirestore, receiver: String, receiverName: String, senderName: String?, date: String?, time: String?, state: String, timestamp: FieldValue): String?
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun changeStateCall(db: FirebaseFirestore, callId: String, state: String): Boolean
    }
}
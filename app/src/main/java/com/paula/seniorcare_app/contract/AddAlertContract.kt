package com.paula.seniorcare_app.contract

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.R
import kotlinx.android.synthetic.main.fragment_add_alert.*
import kotlinx.coroutines.tasks.await
import java.util.*

interface AddAlertContract {
    interface View {
        fun changeColorSelectedDay(day: TextView)
        fun emptyEditText(x: TextInputLayout)
        fun emptyMenu(x: AutoCompleteTextView, y: TextInputLayout)
        fun emptyDays(week:Array<TextView?>): Boolean
        fun emptyDate(): Boolean
        fun errorEmptyDays(week:Array<TextView?>)
        fun errorEmptyDate()
        fun getSelectedDays(week:Array<TextView?>): HashMap<String,Int>
        fun setSelectedDays(week:Array<TextView?>, selectedDays: HashMap<String,Int>)
    }

    interface Presenter {
        suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot?
        suspend fun getAddedRelatives(db: FirebaseFirestore) : QuerySnapshot?
        suspend fun isManagerOfRelative(db: FirebaseFirestore, relativeUid: String): Boolean
        suspend fun getReceiverOfAlertEmail(db: FirebaseFirestore, email: String): QuerySnapshot?
        suspend fun createAlert(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean
        suspend fun updateAlert(db: FirebaseFirestore, id: String, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String, Int>?, date: String?): Boolean
    }
}
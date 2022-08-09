package com.paula.seniorcare_app.view

import android.content.ContentValues
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.OutgoingVideocallContract
import com.paula.seniorcare_app.interactor.OutgoingVideocallInteractor
import com.paula.seniorcare_app.presenter.OutgoingVideocallPresenter
import kotlinx.android.synthetic.main.activity_outgoing_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class OutgoingVideocallActivity : AppCompatActivity(), OutgoingVideocallContract.View {

    lateinit var outgoingVideocallPresenter: OutgoingVideocallPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_videocall)
        outgoingVideocallPresenter = OutgoingVideocallPresenter(this, OutgoingVideocallInteractor())

        var callId: String? = null

        val receiverUid = intent.getStringExtra("uid").toString()
        val receiverImage = intent.getStringExtra("image").toString()
        val receiverName = intent.getStringExtra("name").toString()
        val receiverEmail = intent.getStringExtra("email").toString()

        nameVideocallTextView.text = receiverName
        emailVideocallTextView.text = intent.getStringExtra("email").toString()
        Glide.with(this).load(receiverImage).centerCrop().into(imageVideocallImageView)

        val db = FirebaseFirestore.getInstance()
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val time = if(minute.toString().length == 1 || minute.toString() == "00"){
            "$hour:0$minute"
        } else {
            "$hour:$minute"
        }
        val timestamp = FieldValue.serverTimestamp()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val sender = outgoingVideocallPresenter.getUser(db, FirebaseAuth.getInstance().currentUser!!.uid)
                val senderUid = sender?.get("uid") as String
                val senderName = sender.get("name") as String
                val senderEmail = sender.get("email") as String
                val senderImage = sender.get("image") as String
                val receiver = outgoingVideocallPresenter.getUser(db, receiverUid)
                val receiverToken = receiver?.get("token") as String
                callId = outgoingVideocallPresenter.createCall(db, receiverUid, receiverName, senderName, date, time, "waiting", timestamp)
                val senderJwt = outgoingVideocallPresenter.createCallHttp(senderUid, senderName, senderEmail, senderImage, receiverUid, receiverName, receiverEmail, receiverImage, receiverToken, callId!!).await()
                val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
                val edit: SharedPreferences.Editor = sp.edit()
                edit.putString("JWT",senderJwt)
                edit.apply()
            }
        }

        endCallButton.setOnClickListener{
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    outgoingVideocallPresenter.changeStateCall(db, callId!!, "lost")
                    //HTTP FUNCTION
                    val receiver = outgoingVideocallPresenter.getUser(db, receiverUid)
                    val receiverToken = receiver?.get("token") as String
                    outgoingVideocallPresenter.rejectCallHttp(receiverToken, callId!!)
                }
                finish()
            }
        }
    }
}
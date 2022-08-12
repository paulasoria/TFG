package com.paula.seniorcare_app.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.OutgoingVideocallContract
import com.paula.seniorcare_app.presenter.OutgoingVideocallPresenter
import kotlinx.android.synthetic.main.activity_outgoing_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class OutgoingVideocallActivity : AppCompatActivity(), OutgoingVideocallContract.View {
    private val outgoingVideocallPresenter = OutgoingVideocallPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_videocall)

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

        endVideocallButton.setOnClickListener{
            callId?.let { it1 -> endVideocall(db, it1, receiverUid) }
        }
    }

    override fun endVideocall(db: FirebaseFirestore, callId: String, receiverUid: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                outgoingVideocallPresenter.changeStateCall(db, callId!!, "lost")
                val receiver = outgoingVideocallPresenter.getUser(db, receiverUid)
                val receiverToken = receiver?.get("token") as String
                outgoingVideocallPresenter.rejectCallHttp(receiverToken, callId!!)
            }
            finish()
        }
    }
}
package com.paula.seniorcare_app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.dataclass.Videocall

class VideocallAdapter(private var videocallList: ArrayList<Videocall>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return videocallList.size
    }

    override fun getItem(p0: Int): Any {
        return videocallList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.history_item, null)

        val name : TextView = rootView.findViewById(R.id.nameHistoryTextView)
        val time : TextView = rootView.findViewById(R.id.timeHistoryTextView)
        val info : TextView = rootView.findViewById(R.id.infoHistoryTextView)
        val date : TextView = rootView.findViewById(R.id.dateHistoryTextView)
        val videocall : Videocall = getItem(p0) as Videocall
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        time.text = videocall.time
        date.text = videocall.date
        if(videocall.sender == currentUid){
            info.text = "Llamada saliente"
            name.text = videocall.receiverName
        } else if(videocall.receiver == currentUid){
            if(videocall.state == "accepted") {
                info.text = "Llamada entrante"
            }else if(videocall.state == "rejected") {
                info.text = "Llamada rechazada"
            } else if(videocall.state == "lost"){
                info.text = "Llamada perdida"
            }
            name.text = videocall.senderName
        }

        return rootView
    }
}
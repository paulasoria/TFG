package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData.Item
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.model.Alert
import java.util.HashMap

class AlertsAdapter(private var alertsList: ArrayList<Alert>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return alertsList.size
    }

    override fun getItem(p0: Int): Any {
        return alertsList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.alert_item, null)
        val db = FirebaseFirestore.getInstance()

        val name : TextView = rootView.findViewById(R.id.nameAlertTextView)
        val email : TextView = rootView.findViewById(R.id.emailAlertTextView)
        val time : TextView = rootView.findViewById(R.id.timeAlertTextView)
        val tag : TextView = rootView.findViewById(R.id.tagAlertTextView)
        val date : TextView = rootView.findViewById(R.id.dateTextView)
        val week = arrayOfNulls<TextView>(7)
        week[0] = rootView.findViewById(R.id.lunes)
        week[1] = rootView.findViewById(R.id.martes)
        week[2] = rootView.findViewById(R.id.miercoles)
        week[3] = rootView.findViewById(R.id.jueves)
        week[4] = rootView.findViewById(R.id.viernes)
        week[5] = rootView.findViewById(R.id.sabado)
        week[6] = rootView.findViewById(R.id.domingo)

        val alert : Alert = getItem(p0) as Alert
        val receiver = getReceiver(db, alert.receiver)

        if (receiver != null) {
            name.text = receiver.data?.getValue("name").toString()
            email.text = receiver.data?.getValue("email").toString()
            time.text = alert.time
            tag.text = alert.tag
            date.text = alert.date
            //Week
            //Recorrer alert.daysOfWeek e ir asignando color a cada uno
        }
        return rootView
    }

    private fun getReceiver(db: FirebaseFirestore, receiver: String?): DocumentSnapshot?{
        if (receiver != null) {
            return db.collection("users").document(receiver).get().result
        } else {
            return null
        }
    }
}
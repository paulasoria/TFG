package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.model.Alert

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
        val removeAlertButton : TextView = rootView.findViewById(R.id.removeAlertButton)

        val name : TextView = rootView.findViewById(R.id.nameAlertTextView)
        val email : TextView = rootView.findViewById(R.id.emailAlertTextView)
        val time : TextView = rootView.findViewById(R.id.timeAlertTextView)
        val tag : TextView = rootView.findViewById(R.id.tagAlertTextView)
        val date : TextView = rootView.findViewById(R.id.dateTextView)
        val weeklyLayout : LinearLayout = rootView.findViewById(R.id.weeklyLayout)
        val l : TextView = rootView.findViewById(R.id.lunes)
        val m : TextView = rootView.findViewById(R.id.martes)
        val x : TextView = rootView.findViewById(R.id.miercoles)
        val j : TextView = rootView.findViewById(R.id.jueves)
        val v : TextView = rootView.findViewById(R.id.viernes)
        val s : TextView = rootView.findViewById(R.id.sabado)
        val d : TextView = rootView.findViewById(R.id.domingo)

        val alert : Alert = getItem(p0) as Alert

        name.text = alert.receiverName
        email.text = alert.receiverEmail
        time.text = alert.time
        tag.text = alert.tag

        if(alert.repetition == "weekly"){
            alert.daysOfWeek?.forEach {
                //if(it.value.equals("1")) {
                if(it.value == 1) {
                    when (it.key) {
                        "L" -> { l.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "M" -> { m.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "X" -> { x.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "J" -> { j.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "V" -> { v.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "S" -> { s.setTextColor(context.resources.getColor(R.color.turquoise)) }
                        "D" -> { d.setTextColor(context.resources.getColor(R.color.turquoise)) }
                    }
                }
            }
            weeklyLayout.visibility = View.VISIBLE
            date.visibility = View.INVISIBLE
        } else {    //"eventually"
            date.text = alert.date
            weeklyLayout.visibility = View.INVISIBLE
            date.visibility = View.VISIBLE
        }

        removeAlertButton.setOnClickListener {
            showDeleteAlertDialog(alert)
        }

        return rootView
    }

    private fun showDeleteAlertDialog(alert: Alert){
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.delete_alert))
        builder.setPositiveButton("Aceptar") { _,_ ->
            alertsList.remove(alert)
            deleteAlertFromDB(db, currentUid, alert.id.toString())
            notifyDataSetChanged()
        }
        builder.setNegativeButton("Cancelar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlertFromDB(db: FirebaseFirestore, currentUid: String, id: String){
        db.collection("users").document(currentUid).collection("alerts").document(id).delete()
    }
}
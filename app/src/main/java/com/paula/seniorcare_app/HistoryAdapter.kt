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
import com.paula.seniorcare_app.model.History

class HistoryAdapter(private var historyList: ArrayList<History>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return historyList.size
    }

    override fun getItem(p0: Int): Any {
        return historyList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.alert_item, null)
        val db = FirebaseFirestore.getInstance()

        //Cosas

        return rootView
    }
}
package com.paula.seniorcare_app

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.History
import com.paula.seniorcare_app.model.Videocall
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_history, container, false)

        val historyVideocallButton : Button = view.findViewById(R.id.historyVideocallButton)
        val historyAlertButton : Button = view.findViewById(R.id.historyAlertButton)

        //Poner por defecto el historial de videollamadas:
        historyVideocallButton.setBackgroundColor(resources.getColor(R.color.turquoise))
        setHistoryOfVideocalls()

        historyVideocallButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            setHistoryOfVideocalls()
        }

        historyAlertButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            setHistoryOfAlerts()
        }

        return view
    }

    private fun setHistoryOfVideocalls() {
        val db = FirebaseFirestore.getInstance()
        val videocallsList = ArrayList<Videocall>()
        videocallsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val videocalls = getVideocalls(db)
                videocalls?.iterator()?.forEach { videocall ->
                    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                    if(videocall.data.getValue("sender").toString() == currentUid || videocall.data.getValue("receiver").toString() == currentUid){
                        val id: String = videocall.data.getValue("id").toString()
                        val sender: String = videocall.data.getValue("sender").toString()
                        val senderName: String = videocall.data.getValue("senderName").toString()
                        val receiver: String = videocall.data.getValue("receiver").toString()
                        val receiverName: String = videocall.data.getValue("receiverName").toString()
                        val time: String = videocall.data.getValue("time").toString()
                        val date: String = videocall.data.getValue("date").toString()
                        val state: String = videocall.data.getValue("state").toString()
                        val v = Videocall(id, sender, senderName, receiver, receiverName, date, time, state);
                        videocallsList.add(v)
                    }
                }
            }
            showResultsVideocalls(videocallsList)
        }
    }

    private fun setHistoryOfAlerts(){
        val db = FirebaseFirestore.getInstance()
        val alertsList = ArrayList<History>()
        alertsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val alerts = getHistoryOfAlerts(db)
                alerts?.iterator()?.forEach { alert ->
                    val id: String = alert.data.getValue("id").toString()
                    val receiver: String = alert.data.getValue("receiver").toString()
                    val tag: String = alert.data.getValue("tag").toString() //Etiqueta alerta
                    val time: String = alert.data.getValue("time").toString()
                    val date : String = alert.data.getValue("date").toString()
                    val a = History(id, receiver, time, date, tag)
                    alertsList.add(a)
                }
            }
            showResultsAlerts(alertsList)
        }
    }

    private suspend fun getVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val data = db.collection("videocalls").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF VIDEOCALLS ERROR", e)
            null
        }
    }

    private suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("historyAlerts").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF ALERTS ERROR", e)
            null
        }
    }

    private fun showResultsVideocalls(videocallList: ArrayList<Videocall>){
        val videocallAdapter: VideocallAdapter?
        if(videocallList.isEmpty()){
            noHistoryTextView.visibility = View.VISIBLE
            //noAlertsTextView.text = getString(R.string.no_config_alerts)
            noHistoryTextView.text = "No tienes nada en el historial"
            //QUITAR CONTENIDO DEL ADAPTER ???
            videocallAdapter = VideocallAdapter(videocallList, requireContext())
            historyListView.adapter = videocallAdapter
        } else {
            noHistoryTextView.visibility = View.INVISIBLE
            videocallAdapter = VideocallAdapter(videocallList, requireContext())
            historyListView.adapter = videocallAdapter
        }
    }

    private fun showResultsAlerts(historyList: ArrayList<History>){
        val alertAdapter: HistoryAlertAdapter?
        if(historyList.isEmpty()){
            noHistoryTextView.visibility = View.VISIBLE
            //noAlertsTextView.text = getString(R.string.no_config_alerts)
            noHistoryTextView.text = "No tienes nada en el historial"
            //QUITAR CONTENIDO DEL ADAPTER ???
            alertAdapter = HistoryAlertAdapter(historyList, requireContext())
            historyListView.adapter = alertAdapter
        } else {
            noHistoryTextView.visibility = View.INVISIBLE
            alertAdapter = HistoryAlertAdapter(historyList, requireContext())
            historyListView.adapter = alertAdapter
        }
    }
}
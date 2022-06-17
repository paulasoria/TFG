package com.paula.seniorcare_app

import android.content.ContentValues
import android.os.Bundle
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
        //ListView

        historyVideocallButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            //Mostrar historial de videollamadas recibidas, rechazadas y realizadas en listview
            val db = FirebaseFirestore.getInstance()
            val videocallsList = ArrayList<History>()
            videocallsList.clear()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val videocalls = getHistoryOfVideocalls(db)
                    videocalls?.iterator()?.forEach { videocall ->
                        val id: String = videocall.data.getValue("id").toString()
                        val receiver: String = videocall.data.getValue("receiver").toString()
                        val tag: String = videocall.data.getValue("tag").toString() //Llamada perdida, llamada recibida, etc.
                        val time: String = videocall.data.getValue("time").toString()
                        val date : String = videocall.data.getValue("date").toString()
                        val v = History(id, receiver, time, date, tag)
                        videocallsList.add(v)
                    }
                }
                showResults(videocallsList)
            }
        }

        historyAlertButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.turquoise))
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
                showResults(alertsList)
            }
        }
        return view
    }

    private suspend fun getHistoryOfVideocalls(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            //Cuando se reciba una videollamada se tiene que crear una entrada aquí
            //Se necesita otra función para actualizar el estado cuando se cuelgue o se finalice
            val data = db.collection("users").document(currentUid.toString()).collection("historyOfVideocalls").get().await()
            //val data = db.collection("historyOfVideocalls").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF VIDEOCALLS ERROR", e)
            null
        }
    }

    private suspend fun getHistoryOfAlerts(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            //Cuando se reciba una alerta se tiene que crear una entrada aquí
            val data = db.collection("users").document(currentUid.toString()).collection("historyAlerts").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF ALERTS ERROR", e)
            null
        }
    }

    private fun showResults(historyList: ArrayList<History>){
        val adapter: HistoryAdapter?
        if(historyList.isEmpty()){
            noHistoryTextView.visibility = View.VISIBLE
            //noAlertsTextView.text = getString(R.string.no_config_alerts)
            noHistoryTextView.text = "No tienes nada en el historial"
            //QUITAR CONTENIDO DEL ADAPTER ???
            adapter = HistoryAdapter(historyList, requireContext())     //CREAR ADAPTER
            historyListView.adapter = adapter
        } else {
            noHistoryTextView.visibility = View.INVISIBLE
            adapter = HistoryAdapter(historyList, requireContext())
            historyListView.adapter = adapter
        }
    }
}
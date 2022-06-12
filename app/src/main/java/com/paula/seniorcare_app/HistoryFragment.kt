package com.paula.seniorcare_app

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.Alert
import kotlinx.android.synthetic.main.fragment_alerts.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_history, container, false)

        //Poner por defecto el historial de videollamadas:
        // - Color botón
        // - ListView

        /*historyVideocallButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            //Mostrar historial de videollamadas recibidas, rechazadas y realizadas en listview
        }

        historyAlertButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            //Mostrar historial de alertas recibidas en listview, es decir, receiver=yo y tiempo mayor a hoy
            val db = FirebaseFirestore.getInstance()
            //val currentDate = getCurrentDate()
            //val currentTime = getCurrentTime()
            val alertsList = ArrayList<Alert>()
            alertsList.clear()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val alerts = getHistoryOfAlerts(db)
                    alerts?.iterator()?.forEach { alert ->
                        val id: String = alert.data.getValue("id").toString()
                        val sender: String = alert.data.getValue("sender").toString()
                        val receiver: String = alert.data.getValue("receiver").toString()   //Se necesita o no en función de si hago colección o subcolección
                        val tag: String = alert.data.getValue("tag").toString()
                        val time: String = alert.data.getValue("time").toString()
                        val repetition: String = alert.data.getValue("repetition").toString()
                        if(repetition == "weekly") {
                            val daysOfWeek: HashMap<String, Int> = alert.data.getValue("daysOfWeek") as HashMap<String, Int>
                            val a = Alert(id, null, receiver, null, null, tag, repetition, time, daysOfWeek, null)
                            alertsList.add(a)
                        } else {    //"eventually"
                            val date: String = alert.data.getValue("date").toString()
                            val a = Alert(id, sender, receiver, null, null, tag, repetition, time, null, date)
                            alertsList.add(a)
                        }
                    }
                }
            }
        }*/

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
            val data = db.collection("users").document(currentUid.toString()).collection("historyOfAlerts").whereEqualTo("receiver",currentUid.toString()).get().await()
            //val data = db.collection("historyOfAlerts").get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING HISTORY OF ALERTS ERROR", e)
            null
        }
    }

    private fun showResults(alertsList: ArrayList<Alert>){
        val adapter: AlertsAdapter?
        if(alertsList.isEmpty()){
            noAlertsTextView.visibility = View.VISIBLE
            noAlertsTextView.text = getString(R.string.no_config_alerts)
            //QUITAR CONTENIDO DEL ADAPTER ???
            adapter = AlertsAdapter(alertsList, requireContext())     //CREAR ADAPTER
            alertsListView.adapter = adapter
        } else {
            noAlertsTextView.visibility = View.INVISIBLE
            adapter = AlertsAdapter(alertsList, requireContext())
            alertsListView.adapter = adapter
            //Editar alerta
            /*alertsListView.setOnItemClickListener{ _,_,position,_ ->
                val addAlertFragment = AddAlertFragment()
                val data = Bundle()
                data.putSerializable("alert", alertsList[position])
                addAlertFragment.arguments = data
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,addAlertFragment)?.commit()
            }*/
        }
    }

    /*private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$day/$month/$year"
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return if(minute.toString().length == 1){
            "$hour:0$minute"
        } else {
            "$hour:$minute"
        }
    }*/
}
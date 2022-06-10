package com.paula.seniorcare_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.Alert
import com.paula.seniorcare_app.model.Petition
import kotlinx.android.synthetic.main.fragment_alerts.*
import kotlinx.android.synthetic.main.fragment_petitions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.HashMap

class AlertsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_alerts, container, false)
        val addAlertButton:FloatingActionButton = view.findViewById(R.id.addAlertButton)
        val addAlertFragment = AddAlertFragment()

        addAlertButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,addAlertFragment)?.commit()
        }

        getConfiguredAlerts()

        return view
    }

    private fun getConfiguredAlerts(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val alertsList = ArrayList<Alert>()
        alertsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (uid != null) {
                    val alerts = getConfiguredAlertsFromDB(db, uid)
                    alerts?.iterator()?.forEach { alert ->
                        val id: String = alert.data.getValue("id").toString()
                        val sender: String = alert.data.getValue("sender").toString()
                        val receiver: String = alert.data.getValue("receiver").toString()
                        var tag: String = alert.data.getValue("tag").toString()
                        var repetition: String = alert.data.getValue("repetition").toString()
                        var time: String = alert.data.getValue("time").toString()
                        var daysOfWeek: HashMap<String, Int> = alert.data.getValue("daysOfWeek") as HashMap<String, Int>
                        var date: String = alert.data.getValue("date").toString()
                        val a = Alert(id, sender, receiver, tag, repetition, time, daysOfWeek, date)
                        alertsList.add(a)
                    }
                }
            }
            showResults(alertsList)
        }
    }

    private suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return try {
            val data = db.collection("users").document(uid).collection("alerts").whereEqualTo("sender",uid).get().await()
            data
        } catch (e: Exception) {
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
        }
    }
}
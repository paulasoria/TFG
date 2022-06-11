package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.Alert
import kotlinx.android.synthetic.main.fragment_alerts.*
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
        val alertsList = ArrayList<Alert>()
        alertsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val alerts = getConfiguredAlertsFromDB(db)
                alerts?.iterator()?.forEach { alert ->
                    val id: String = alert.data.getValue("id").toString()
                    val sender: String = alert.data.getValue("sender").toString()
                    val receiver: String = alert.data.getValue("receiver").toString()
                    val receiverUser = getReceiverOfAlert(db, receiver)
                    val receiverName : String = receiverUser?.data?.getValue("name").toString()
                    val receiverEmail : String = receiverUser?.data?.getValue("email").toString()
                    val tag: String = alert.data.getValue("tag").toString()
                    val repetition: String = alert.data.getValue("repetition").toString()
                    val time: String = alert.data.getValue("time").toString()
                    if(repetition.equals("weekly")){
                        val daysOfWeek: HashMap<String, Int> = alert.data.getValue("daysOfWeek") as HashMap<String, Int>
                        val a = Alert(id, sender, receiver, receiverName, receiverEmail, tag, repetition, time, daysOfWeek, null)
                        alertsList.add(a)
                    } else {    //"eventually"
                        val date: String = alert.data.getValue("date").toString()
                        val a = Alert(id, sender, receiver, receiverName, receiverEmail, tag, repetition, time, null, date)
                        alertsList.add(a)
                    }
                    //}
                }
            }
            showResults(alertsList)
        }
    }

    private suspend fun getReceiverOfAlert(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try{
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getConfiguredAlertsFromDB(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            //val data = db.collection("users").document(uid).collection("alerts").get().await()
            val data = db.collection("users").document(currentUid.toString())
                .collection("alerts").whereEqualTo("sender",currentUid.toString()).get().await()
            data
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "GETTING CONFIGURED ALERTS ERROR", e)
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
}
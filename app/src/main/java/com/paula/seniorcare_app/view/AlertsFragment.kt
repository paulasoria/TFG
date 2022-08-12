package com.paula.seniorcare_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.adapter.AlertsAdapter
import com.paula.seniorcare_app.contract.AlertsContract
import com.paula.seniorcare_app.dataclass.Alert
import com.paula.seniorcare_app.presenter.AlertsPresenter
import kotlinx.android.synthetic.main.fragment_alerts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertsFragment : Fragment(), AlertsContract.View {
    private val alertsPresenter = AlertsPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_alerts, container, false)
        val addAlertButton:FloatingActionButton = view.findViewById(R.id.addAlertButton)
        val addAlertFragment = AddAlertFragment()

        showConfiguredAlerts()

        addAlertButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,addAlertFragment)?.commit()
        }

        return view
    }

    override fun showConfiguredAlerts() {
        val db = FirebaseFirestore.getInstance()
        val alertsList = ArrayList<Alert>()
        alertsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val alerts = alertsPresenter.getConfiguredAlerts(db)
                alerts?.iterator()?.forEach { alert ->
                    val id: String = alert.data.getValue("id").toString()
                    val sender: String = alert.data.getValue("sender").toString()
                    val receiver: String = alert.data.getValue("receiver").toString()
                    val receiverUser = alertsPresenter.getReceiverOfAlertUid(db, receiver)
                    val receiverName : String = receiverUser?.data?.getValue("name").toString()
                    val receiverEmail : String = receiverUser?.data?.getValue("email").toString()
                    val tag: String = alert.data.getValue("tag").toString()
                    val repetition: String = alert.data.getValue("repetition").toString()
                    val time: String = alert.data.getValue("time").toString()
                    if(repetition == "weekly") {
                        val daysOfWeek: HashMap<String, Int> = alert.data.getValue("daysOfWeek") as HashMap<String, Int>
                        val a = Alert(id, sender, receiver, receiverName, receiverEmail, tag, repetition, time, daysOfWeek, null)
                        alertsList.add(a)
                    } else {    //"eventually"
                        val date: String = alert.data.getValue("date").toString()
                        val a = Alert(id, sender, receiver, receiverName, receiverEmail, tag, repetition, time, null, date)
                        alertsList.add(a)
                    }
                }
            }
            showResults(alertsList)
        }
    }

    override fun showResults(alertsList: ArrayList<Alert>) {
        val adapter: AlertsAdapter?
        if(alertsList.isEmpty()){
            noAlertsTextView.visibility = View.VISIBLE
            noAlertsTextView.text = getString(R.string.no_config_alerts)
            adapter = AlertsAdapter(alertsList, requireContext())
            alertsListView.adapter = adapter
        } else {
            noAlertsTextView.visibility = View.INVISIBLE
            adapter = AlertsAdapter(alertsList, requireContext())
            alertsListView.adapter = adapter
            alertsListView.setOnItemClickListener{ _,_,position,_ ->
                val addAlertFragment = AddAlertFragment()
                val data = Bundle()
                data.putSerializable("alert", alertsList[position])
                addAlertFragment.arguments = data
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,addAlertFragment)?.commit()
            }
        }
    }
}
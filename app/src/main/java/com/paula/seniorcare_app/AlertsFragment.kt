package com.paula.seniorcare_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlertsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_alerts, container, false)
        val addAlertButton:FloatingActionButton = view.findViewById(R.id.addAlertButton)
        val addAlertFragment = AddAlertFragment()

        addAlertButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,addAlertFragment)?.commit()
        }

        return view
    }
}
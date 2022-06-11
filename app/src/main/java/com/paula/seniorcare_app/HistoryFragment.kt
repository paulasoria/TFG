package com.paula.seniorcare_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_history, container, false)

        //Poner por defecto el historial de videollamadas:
        // - Color botón
        // - ListView

        historyVideocallButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            //Mostrar historial de videollamadas en listview
        }

        historyAlertButton.setOnClickListener {
            historyVideocallButton.setBackgroundColor(resources.getColor(R.color.dark_blue))
            historyAlertButton.setBackgroundColor(resources.getColor(R.color.turquoise))
            //Mostrar historial de alertas en listview
        }

        return view
    }

    //getHistoryOfVideocalls()

    //getHistoryOfAlerts()
}
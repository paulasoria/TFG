package com.paula.seniorcare_app

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import java.util.*

class AddAlertFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_alert, container, false)
        val setHourButton: Button = view.findViewById(R.id.setHourButton)

        setHourButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(context, { picker, hour, minute ->
                //Guardar datos alarma
                //val id = UUID.randomUUID().toString()
                //val alert = Alert(id, sender, receiver, tag, repetition, hour, minute, dayOfWeek, date, isOn)
                //Poner datos en el texto del botón
                setHourButton.text = "$hour:$minute"
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        return view
    }
}
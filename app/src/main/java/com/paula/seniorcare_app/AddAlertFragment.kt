package com.paula.seniorcare_app

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_add_alert.*
import java.util.*

class AddAlertFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_alert, container, false)
        val setHourButton: Button = view.findViewById(R.id.setHourButton)
        val repetitionMenuTextView : AutoCompleteTextView = view.findViewById(R.id.repetitionMenuTextView)
        val saveAlertButton : Button = view.findViewById(R.id.saveAlertButton)
        val relativeTextInput : TextInputLayout = view.findViewById(R.id.relativeTextInput)
        val tagTextInput : TextInputLayout = view.findViewById(R.id.tagTextInput)
        val repetitionMenu : TextInputLayout = view.findViewById(R.id.repetitionMenu)
        val l : TextView = view.findViewById(R.id.lunes)
        val m : TextView = view.findViewById(R.id.martes)
        val x : TextView = view.findViewById(R.id.miercoles)
        val j : TextView = view.findViewById(R.id.jueves)
        val v : TextView = view.findViewById(R.id.viernes)
        val s : TextView = view.findViewById(R.id.sabado)
        val d : TextView = view.findViewById(R.id.domingo)
        val daysError : TextView = view.findViewById(R.id.daysError)

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

        val repetition = resources.getStringArray(R.array.repetition)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.lista_menu,
            repetition
        )
        repetitionMenuTextView.setAdapter(adapter)

        changeColorSelectedDay(l)
        changeColorSelectedDay(m)
        changeColorSelectedDay(x)
        changeColorSelectedDay(j)
        changeColorSelectedDay(v)
        changeColorSelectedDay(s)
        changeColorSelectedDay(d)

        saveAlertButton.setOnClickListener {
            val relative = relativeTextInput.editText?.text.toString()
            val tag = tagTextInput.editText?.text.toString()
            val repetition = repetitionMenuTextView.text.toString()
            if (relative.trim().isNotEmpty() && tag.trim().isNotEmpty() && repetition.trim().isNotEmpty() && !emptyDays(l,m,x,j,v,s,d)) {
                //Guardar alerta
            } else {
                emptyEditText(relativeTextInput)
                emptyEditText(tagTextInput)
                if(repetitionMenuTextView.text.toString().trim().isEmpty()){
                    repetitionMenu.error = getString(R.string.empty_field)
                } else { repetitionMenu.error = null }
                errorEmptyDays(l,m,x,j,v,s,d, daysError)
            }

            if(weeklyLayout.visibility == View.INVISIBLE ){
                weeklyLayout.visibility = View.VISIBLE
                calendarLayout.visibility = View.INVISIBLE
            } else {
                weeklyLayout.visibility = View.INVISIBLE
                calendarLayout.visibility = View.VISIBLE
            }

        }

        return view
    }

    private fun changeColorSelectedDay(day: TextView){
        day.setOnClickListener {
            if(day.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)) {
                day.setTextColor(resources.getColor(R.color.turquoise))
            } else {
                day.setTextColor(resources.getColor(R.color.super_light_grayish_blue))
            }
        }
    }

    private fun emptyEditText(x: TextInputLayout) {
        if(x.editText?.text.toString().trim().isEmpty()){
            x.error = getString(R.string.empty_field)
        } else { x.error = null }
    }

    private fun emptyDays(l:TextView, m:TextView, x:TextView, j:TextView, v:TextView, s:TextView, d:TextView): Boolean {
        return l.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                m.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                x.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                j.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                v.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                s.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
                d.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)
    }

    private fun errorEmptyDays(l:TextView, m:TextView, x:TextView, j:TextView, v:TextView, s:TextView, d:TextView, daysError:TextView) {
        if(l.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            m.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            x.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            j.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            v.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            s.currentTextColor == resources.getColor(R.color.super_light_grayish_blue) &&
            d.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)) {
            daysError.visibility = View.VISIBLE
        } else {
            daysError.visibility = View.INVISIBLE
        }
    }
}
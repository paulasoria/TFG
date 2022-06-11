package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.Alert
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_add_alert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.util.*

class AddAlertFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_alert, container, false)
        val setHourButton: Button = view.findViewById(R.id.setHourButton)
        val setDateButton: Button = view.findViewById(R.id.setDateButton)
        val repetitionMenuTextView : AutoCompleteTextView = view.findViewById(R.id.repetitionMenuTextView)
        val saveAlertButton : Button = view.findViewById(R.id.saveAlertButton)
        val relativeMenuTextView : AutoCompleteTextView = view.findViewById(R.id.relativeMenuTextView)
        val tagTextInput : TextInputLayout = view.findViewById(R.id.tagTextInput)
        val repetitionMenu : TextInputLayout = view.findViewById(R.id.repetitionMenu)
        var selectedItem : String? = null
        val week = arrayOfNulls<TextView>(7)
        week[0] = view.findViewById(R.id.lunes)
        week[1] = view.findViewById(R.id.martes)
        week[2] = view.findViewById(R.id.miercoles)
        week[3] = view.findViewById(R.id.jueves)
        week[4] = view.findViewById(R.id.viernes)
        week[5] = view.findViewById(R.id.sabado)
        week[6] = view.findViewById(R.id.domingo)

        val calendar = Calendar.getInstance()
        setHourButton.setOnClickListener {
            TimePickerDialog(context, { _, hour, minute ->
                if(minute.toString().length == 1){
                    setHourButton.text = "$hour:0$minute"
                }else {
                    setHourButton.text = "$hour:$minute"
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        setDateButton.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                setDateButton.text = "$day/$month/$year"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val db = FirebaseFirestore.getInstance()
        var relatives : QuerySnapshot?
        val addedRelativesList = ArrayList<String>()
        addedRelativesList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                relatives = getAddedRelativesList(db)
                relatives?.iterator()?.forEach { relative ->
                    val email: String = relative.data.getValue("email").toString()
                    addedRelativesList.add(email)
                }
            }
            val adapterRel = ArrayAdapter(
                requireContext(),
                R.layout.lista_menu,
                addedRelativesList
            )
            relativeMenuTextView.setAdapter(adapterRel)
        }

        val repetition = resources.getStringArray(R.array.repetition)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.lista_menu,
            repetition
        )
        repetitionMenuTextView.setAdapter(adapter)
        repetitionMenuTextView.setOnItemClickListener { parent, _, position, _ ->
            selectedItem = parent.getItemAtPosition(position).toString()
            if(selectedItem == "Semanal"){
                weeklyLayout.visibility = View.VISIBLE
                calendarLayout.visibility = View.INVISIBLE
            } else if(selectedItem == "Eventual"){    //Eventual
                weeklyLayout.visibility = View.INVISIBLE
                calendarLayout.visibility = View.VISIBLE
            }
        }

        week.forEach {
            if (it != null) {
                changeColorSelectedDay(it)
            }
        }

        saveAlertButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val relative = relativeMenuTextView.text.toString()
            val tag = tagTextInput.editText?.text.toString()
            val repetition = repetitionMenuTextView.text.toString()
            if (relative.trim().isNotEmpty() && tag.trim().isNotEmpty() && repetition.trim().isNotEmpty() && selectedItem != null && selectedItem == "Semanal" && !emptyDays(week)) {
                    val daysOfWeek = getSelectedDays(week)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val userRelative = getReceiverOfAlert(db,relative)
                            userRelative?.iterator()?.forEach { relative ->
                                val receiverUid = relative.data.getValue("uid").toString()
                                if(repetition.equals("Semanal")) {
                                    createAlertInDatabase(db, receiverUid, tag, "weekly", setHourButton.text.toString(), daysOfWeek, null)
                                } else {
                                    createAlertInDatabase(db, receiverUid, tag, "eventually", setHourButton.text.toString(), daysOfWeek, null)
                                }
                            }
                        }
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,AlertsFragment())?.commit()
                    }
                    //Guardar alerta como alarma semanal (y en el otro usuario con Google Functions)
            } else {
                emptyMenu(relativeMenuTextView, relativeMenu)
                emptyEditText(tagTextInput)
                emptyMenu(repetitionMenuTextView, repetitionMenu)
                errorEmptyDays(week)
            }

            if(relative.trim().isNotEmpty() && tag.trim().isNotEmpty() && repetition.trim().isNotEmpty() && selectedItem != null && selectedItem == "Eventual" && !emptyDate()) {
                val date = setDateButton.text.toString()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val userRelative = getReceiverOfAlert(db,relative)
                        userRelative?.iterator()?.forEach { relative ->
                            val receiverUid = relative.data.getValue("uid").toString()
                            if(repetition.equals("Semanal")) {
                                createAlertInDatabase(db, receiverUid, tag, "weekly", setHourButton.text.toString(), null, date)
                            } else {
                                createAlertInDatabase(db, receiverUid, tag, "eventually", setHourButton.text.toString(), null, date)
                            }
                        }
                    }
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,AlertsFragment())?.commit()
                }
                //Guardar alerta como recordatorio eventual (y en el otro usuario con Google Functions)
            } else {
                emptyMenu(relativeMenuTextView, relativeMenu)
                emptyEditText(tagTextInput)
                emptyMenu(repetitionMenuTextView, repetitionMenu)
                errorEmptyDate()
            }
        }

        //Editar alerta
        /*val data : Bundle? = arguments
        if(data != null) {
            val alert: Alert = data.getSerializable("alert") as Alert
            setHourButton.text = alert.time.toString()
            relativeMenuTextView.setText(alert.receiverEmail.toString())
            tagTextInput.editText?.setText(alert.tag.toString())
            if(alert.repetition == "weekly"){
                repetitionMenuTextView.setText("Semanal")
                weeklyLayout.visibility = View.VISIBLE
                calendarLayout.visibility = View.INVISIBLE
                //Datos dias
            } else {    //"eventually"
                repetitionMenuTextView.setText("Eventual")
                weeklyLayout.visibility = View.INVISIBLE
                calendarLayout.visibility = View.VISIBLE
                //Datos fecha
            }
        }*/

        return view
    }

    private suspend fun getAddedRelativesList(db: FirebaseFirestore) : QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getReceiverOfAlert(db: FirebaseFirestore, email: String): QuerySnapshot? {
        return try {
            val user = db.collection("users").whereEqualTo("email", email).get().await()    //No saca ningun resultado???
            user
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "GETTING RECEIVER OF ALERT ERROR", e)
            null
        }
    }

    private suspend fun createAlertInDatabase(db: FirebaseFirestore, receiver: String, tag: String, repetition: String, time: String, daysOfWeek: HashMap<String,Int>?, date: String?): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(sender).collection("alerts").document(id).set(hashMapOf("id" to id, "sender" to sender, "receiver" to receiver, "tag" to tag, "repetition" to repetition, "time" to time, "daysOfWeek" to daysOfWeek, "date" to date)).await()
            //Cloud function para guardar la alerta en el otro familiar (el que recibe notificacion)
            true
        } catch (e: Exception){
            Log.e(ContentValues.TAG, "CREATING ALERT IN DATABASE ERROR", e)
            false
        }
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

    private fun emptyMenu(x: AutoCompleteTextView, y: TextInputLayout){
        if(x.text.toString().trim().isEmpty()){
            y.error = getString(R.string.empty_field)
        } else { y.error = null }
    }

    private fun emptyDays(week:Array<TextView?>): Boolean {
        var numEmpty = 0
        week.forEach {
            if (it != null) {
                if(it.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)){
                    numEmpty += 1
                }
            }
        }
        return numEmpty == week.size
    }

    private fun emptyDate(): Boolean {
        if(setDateButton.text == "00/00/0000"){
            return true
        }
        return false
    }

    private fun errorEmptyDays(week:Array<TextView?>) {
        var numEmpty = 0
        week.forEach {
            if (it != null) {
                if(it.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)){
                    numEmpty += 1
                }
            }
        }
        if(numEmpty == week.size){
            daysError.visibility = View.VISIBLE
        } else {
            daysError.visibility = View.INVISIBLE
        }
    }

    private fun errorEmptyDate(){
        if(setDateButton.text == "00/00/0000"){
            dateError.visibility = View.VISIBLE
        } else {
            dateError.visibility = View.INVISIBLE
        }
    }

    private fun getSelectedDays(week:Array<TextView?>): HashMap<String,Int> {
        var selectedDays: HashMap<String,Int> = hashMapOf("L" to 0, "M" to 0, "X" to 0, "J" to 0, "V" to 0, "S" to 0, "D" to 0)

        week.forEach {
            if (it != null) {
                if (it.currentTextColor == resources.getColor(R.color.turquoise)) {
                    selectedDays[it.text.toString()] = 1
                }
            }
        }
        return selectedDays
    }
}
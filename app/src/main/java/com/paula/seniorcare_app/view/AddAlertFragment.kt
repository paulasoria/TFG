package com.paula.seniorcare_app.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.AddAlertContract
import com.paula.seniorcare_app.interactor.AddAlertInteractor
import com.paula.seniorcare_app.dataclass.Alert
import com.paula.seniorcare_app.presenter.AddAlertPresenter
import kotlinx.android.synthetic.main.fragment_add_alert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddAlertFragment : Fragment(), AddAlertContract.View {

    lateinit var addAlertPresenter: AddAlertPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addAlertPresenter = AddAlertPresenter(this, AddAlertInteractor())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_alert, container, false)
        val setHourButton: Button = view.findViewById(R.id.setHourButton)
        val setDateButton: Button = view.findViewById(R.id.setDateButton)
        val repetitionMenuTextView : AutoCompleteTextView = view.findViewById(R.id.repetitionMenuTextView)
        val saveAlertButton : Button = view.findViewById(R.id.saveAlertButton)
        val relativeMenuTextView : AutoCompleteTextView = view.findViewById(R.id.relativeMenuTextView)
        val tagTextInput : TextInputLayout = view.findViewById(R.id.tagTextInput)
        val repetitionMenu : TextInputLayout = view.findViewById(R.id.repetitionMenu)
        val weeklyLayout : LinearLayout = view.findViewById(R.id.weeklyLayout)
        val calendarLayout : LinearLayout = view.findViewById(R.id.calendarLayout)
        var selectedItem : String? = null
        val week = arrayOfNulls<TextView>(7)
        week[0] = view.findViewById(R.id.lunes)
        week[1] = view.findViewById(R.id.martes)
        week[2] = view.findViewById(R.id.miercoles)
        week[3] = view.findViewById(R.id.jueves)
        week[4] = view.findViewById(R.id.viernes)
        week[5] = view.findViewById(R.id.sabado)
        week[6] = view.findViewById(R.id.domingo)
        
        var alertId : String? = null

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
                setDateButton.text = "$day/${month+1}/$year"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val db = FirebaseFirestore.getInstance()
        var relatives : QuerySnapshot?
        val addedRelativesList = ArrayList<String>()
        addedRelativesList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                relatives = addAlertPresenter.getAddedRelativesList(db)
                relatives?.iterator()?.forEach { relative ->
                    val relativeUid: String = relative.data.getValue("uid").toString()
                    if(addAlertPresenter.isManagerOfRelative(db, relativeUid)) {
                        val email: String = relative.data.getValue("email").toString()
                        addedRelativesList.add(email)
                    }
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

        //Editar alerta
        val editing : Bundle? = arguments
        if(editing != null) {
            val alert: Alert = editing.getSerializable("alert") as Alert
            alertId = alert.id.toString()
            setHourButton.text = alert.time.toString()
            relativeMenuTextView.setText(alert.receiverEmail.toString())
            tagTextInput.editText?.setText(alert.tag.toString())
            relativeMenuTextView.isEnabled = false
            if(alert.repetition == "weekly"){
                selectedItem = "Semanal"
                repetitionMenuTextView.setText("Semanal")
                weeklyLayout.visibility = View.VISIBLE
                calendarLayout.visibility = View.INVISIBLE
                setSelectedDays(week, alert.daysOfWeek!!)
                val repetition = resources.getStringArray(R.array.repetition)
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.lista_menu,
                    repetition
                )
                repetitionMenuTextView.setAdapter(adapter)
            } else {    //"eventually"
                selectedItem = "Eventual"
                repetitionMenuTextView.setText("Eventual")
                weeklyLayout.visibility = View.INVISIBLE
                calendarLayout.visibility = View.VISIBLE
                setDateButton.text = alert.date.toString()
                val repetition = resources.getStringArray(R.array.repetition)
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.lista_menu,
                    repetition
                )
                repetitionMenuTextView.setAdapter(adapter)
            }
        }

        saveAlertButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val relative = relativeMenuTextView.text.toString()
            val tag = tagTextInput.editText?.text.toString()
            val time = setHourButton.text.toString()
            val repetition = repetitionMenuTextView.text.toString()
            if (relative.trim().isNotEmpty() && tag.trim().isNotEmpty() && repetition.trim().isNotEmpty() && selectedItem == "Semanal" && !emptyDays(week)) {
                    val daysOfWeek = getSelectedDays(week)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val userRelative = addAlertPresenter.getReceiverOfAlert(db,relative)
                            userRelative?.iterator()?.forEach { relative ->
                                val receiverUid = relative.data.getValue("uid").toString()
                                if(editing != null){
                                    addAlertPresenter.updateAlertInDatabase(db, alertId!!, receiverUid, tag, "weekly", time, daysOfWeek, null)
                                } else{
                                    addAlertPresenter.createAlertInDatabase(db, receiverUid, tag, "weekly", time, daysOfWeek, null)
                                }
                            }
                        }
                        activity?.supportFragmentManager?.beginTransaction()?.replace(
                            R.id.wrapper,
                            AlertsFragment()
                        )?.commit()
                    }
            } else {
                emptyMenu(relativeMenuTextView, relativeMenu)
                emptyEditText(tagTextInput)
                emptyMenu(repetitionMenuTextView, repetitionMenu)
                errorEmptyDays(week)
            }

            if(relative.trim().isNotEmpty() && tag.trim().isNotEmpty() && repetition.trim().isNotEmpty() && selectedItem == "Eventual" && !emptyDate()) {
                val date = setDateButton.text.toString()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val userRelative = addAlertPresenter.getReceiverOfAlert(db,relative)
                        userRelative?.iterator()?.forEach { relative ->
                            val receiverUid = relative.data.getValue("uid").toString()
                            if(editing != null){
                                addAlertPresenter.updateAlertInDatabase(db, alertId!!, receiverUid, tag, "eventually", time, null, date)
                            } else{
                                addAlertPresenter.createAlertInDatabase(db, receiverUid, tag, "eventually", time, null, date)
                            }
                        }
                    }
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.wrapper,
                        AlertsFragment()
                    )?.commit()
                }
            } else {
                emptyMenu(relativeMenuTextView, relativeMenu)
                emptyEditText(tagTextInput)
                emptyMenu(repetitionMenuTextView, repetitionMenu)
                errorEmptyDate()
            }
        }
        return view
    }

    override fun changeColorSelectedDay(day: TextView){
        day.setOnClickListener {
            if(day.currentTextColor == resources.getColor(R.color.super_light_grayish_blue)) {
                day.setTextColor(resources.getColor(R.color.turquoise))
            } else {
                day.setTextColor(resources.getColor(R.color.super_light_grayish_blue))
            }
        }
    }

    override fun emptyEditText(x: TextInputLayout) {
        if(x.editText?.text.toString().trim().isEmpty()){
            x.error = getString(R.string.empty_field)
        } else { x.error = null }
    }

    override fun emptyMenu(x: AutoCompleteTextView, y: TextInputLayout){
        if(x.text.toString().trim().isEmpty()){
            y.error = getString(R.string.empty_field)
        } else { y.error = null }
    }

    override fun emptyDays(week:Array<TextView?>): Boolean {
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

    override fun emptyDate(): Boolean {
        if(setDateButton.text == "00/00/0000"){
            return true
        }
        return false
    }

    override fun errorEmptyDays(week:Array<TextView?>) {
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

    override fun errorEmptyDate(){
        if(setDateButton.text == "00/00/0000"){
            dateError.visibility = View.VISIBLE
        } else {
            dateError.visibility = View.INVISIBLE
        }
    }

    override fun getSelectedDays(week:Array<TextView?>): HashMap<String,Int> {
        val selectedDays: HashMap<String,Int> = hashMapOf("L" to 0, "M" to 0, "X" to 0, "J" to 0, "V" to 0, "S" to 0, "D" to 0)

        week.forEach {
            if (it != null) {
                if (it.currentTextColor == resources.getColor(R.color.turquoise)) {
                    selectedDays[it.text.toString()] = 1
                }
            }
        }
        return selectedDays
    }

    override fun setSelectedDays(week:Array<TextView?>, selectedDays: HashMap<String,Int>) {
        week.forEach {
            if (it != null) {
                if (selectedDays[it.text.toString()].toString() == "1") {
                    it.setTextColor(resources.getColor(R.color.turquoise))
                }
            }
        }
    }
}
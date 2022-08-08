package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.RelativeProfileContract
import com.paula.seniorcare_app.interactor.RelativeProfileInteractor
import com.paula.seniorcare_app.dataclass.User
import com.paula.seniorcare_app.presenter.RelativeProfilePresenter
import kotlinx.android.synthetic.main.activity_relative_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RelativeProfileActivity : AppCompatActivity(), RelativeProfileContract.View {

    lateinit var relativeProfilePresenter: RelativeProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relative_profile)
        relativeProfilePresenter = RelativeProfilePresenter(this, RelativeProfileInteractor())

        val user = intent.getSerializableExtra("user") as User
        val relativeUid = user.uid.toString()
        nameTextView.text = user.name.toString()
        emailTextView.text = user.email.toString()
        roleTextView.text = user.role.toString()
        Glide.with(this).load(user.image.toString()).centerCrop().into(profileImageView)

        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                if(relativeProfilePresenter.relativeIsManager(relativeUid)){
                    withContext(Dispatchers.Main){
                        gestorCheckBox.isChecked = true
                    }
                } else {
                    withContext(Dispatchers.Main){
                        gestorCheckBox.isChecked = false
                    }
                }

                if(relativeProfilePresenter.relativeIsAdded(relativeUid)) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    val db = FirebaseFirestore.getInstance()
                    val currentUser = relativeProfilePresenter.getUser(db, uid)
                    withContext(Dispatchers.Main) {
                        videocallButton.visibility = View.VISIBLE
                        deleteRelativeButton.visibility = View.VISIBLE
                        addRelativeButton.visibility = View.INVISIBLE
                        if(currentUser?.get("role") == "Familiar"){
                            if(user.role == "Administrador") {
                                gestorCheckBox.visibility = View.VISIBLE
                            } else {
                                gestorCheckBox.visibility = View.INVISIBLE
                            }
                        } else {
                            gestorCheckBox.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        videocallButton.visibility = View.INVISIBLE
                        deleteRelativeButton.visibility = View.INVISIBLE
                        addRelativeButton.visibility = View.VISIBLE
                        gestorCheckBox.visibility = View.INVISIBLE
                    }
                    if (relativeProfilePresenter.petitionIsPendingByReceiver(relativeUid)?.isEmpty == true && relativeProfilePresenter.petitionIsPendingBySender(relativeUid)?.isEmpty == true) { //No peticion pendiente
                        withContext(Dispatchers.Main) {
                            addRelativeButton.isEnabled = true
                        }
                    } else {    //Peticion pendiente
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Solicitud de familiar pendiente", Toast.LENGTH_SHORT).show()
                            addRelativeButton.isEnabled = false
                        }
                    }
                }
            }
        }

        addRelativeButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    relativeProfilePresenter.createPetitionInDatabase(db, currentUid, relativeUid)   //sender, receiver
                }
            }
            Toast.makeText(this, "Solicitud de familiar enviada", Toast.LENGTH_SHORT).show()
            addRelativeButton.isEnabled = false
        }

        deleteRelativeButton.setOnClickListener {
            showDeleteRelativeDialog(relativeUid)
        }

        videocallButton.setOnClickListener {
            val intent = Intent(baseContext, OutgoingVideocallActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("uid", user.uid.toString())
            intent.putExtra("name", user.name.toString())
            intent.putExtra("email", user.email.toString())
            intent.putExtra("image", user.image.toString())
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        val user = intent.getSerializableExtra("user") as User
        val relativeUid = user.uid.toString()
        val db = FirebaseFirestore.getInstance()
        if(gestorCheckBox.isChecked){
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    relativeProfilePresenter.setManagerOnDatabase(db, relativeUid)
                }
            }
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    relativeProfilePresenter.deleteManagerOnDatabase(db, relativeUid)
                }
            }
        }
    }

    override fun showDeleteRelativeDialog(relativeUid: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_relative))
        builder.setPositiveButton("Aceptar") { _,_ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    relativeProfilePresenter.deleteRelativeFromDB(db, currentUid, relativeUid)
                }
            }
        }
        builder.setNegativeButton("Cancelar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
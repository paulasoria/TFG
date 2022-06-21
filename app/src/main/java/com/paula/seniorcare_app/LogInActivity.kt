package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_auth.logInButton
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.emailTextInput
import kotlinx.android.synthetic.main.activity_log_in.passwordTextInput
import kotlinx.android.synthetic.main.activity_reset_password.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        setup()
    }

    private fun setup() {
        logInButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val email = emailTextInput.editText?.text.toString()
            val password = passwordTextInput.editText?.text.toString()
            if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val user = getUser(db, uid)
                            if(user?.get("role") == "Administrador"){
                                showHome()
                            } else {    //Familiar
                                showTv()
                            }
                        }
                    }
                }.addOnFailureListener {
                    showAlertLogIn()
                }
            } else {
                emptyEditText(emailTextInput)
                emptyEditText(passwordTextInput)
            }
        }

        resetPasswordButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_reset_password, null)
            builder.setView(view)
            builder.setTitle("Contraseña olvidada")
            builder.setMessage("Escribe tu email y te enviaremos un correo para reestablecer tu contraseña")
            builder.setPositiveButton("Aceptar") { _,_ ->
                if (view.resetPasswordEditText.text.toString().isNotEmpty()){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(view.resetPasswordEditText.text.toString()).addOnSuccessListener {
                        Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar",null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            null
        }
    }

    private fun emptyEditText(x: TextInputLayout) {
        if(x.editText?.text.toString().trim().isEmpty()){
            x.error = getString(R.string.empty_field)
        } else { x.error = null }
    }

    private fun showAlertLogIn(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.login_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {
        val homeIntent = Intent(this,HomeActivity::class.java)
        startActivity(homeIntent)
    }

    private fun showTv(){
        val intent = Intent(this,TvActivity::class.java)
        startActivity(intent)
    }
}
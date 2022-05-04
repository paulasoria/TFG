package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.logInButton
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_reset_password.view.*

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        setup()
    }

    private fun setup() {
        title = "Inicio de sesión"
        logInButton.setOnClickListener {
            if (email.editText?.text.toString().trim().isNotEmpty() && password.editText?.text.toString().trim().isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.editText?.text.toString(), password.editText?.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Guardado de datos
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", email.editText?.text.toString())
                        prefs.apply()
                        showHome()
                    } else {
                        showAlertLogIn()
                    }
                }
            } else {
                if (email.editText?.text.toString().trim().isEmpty()){
                    email.error = getString(R.string.empty_field)
                } else { email.error = null }

                if (password.editText?.text.toString().trim().isEmpty()) {
                    password.error = getString(R.string.empty_field)
                } else { password.error = null }
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
                    FirebaseAuth.getInstance().sendPasswordResetEmail(view.resetPasswordEditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            builder.setNegativeButton("Cancelar",null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
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
}
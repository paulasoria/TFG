package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.LogInContract
import com.paula.seniorcare_app.presenter.LogInPresenter
import kotlinx.android.synthetic.main.activity_auth.logInButton
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.emailTextInput
import kotlinx.android.synthetic.main.activity_log_in.passwordTextInput
import kotlinx.android.synthetic.main.activity_reset_password.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LogInActivity : AppCompatActivity(), LogInContract.View {
    private val logInPresenter = LogInPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        logInButton.setOnClickListener {
            val email = emailTextInput.editText?.text.toString()
            val password = passwordTextInput.editText?.text.toString()
            if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (logInPresenter.logIn(email, password)) {
                            val db = FirebaseFirestore.getInstance()
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val user = logInPresenter.getUser(db, uid)
                            if (user?.get("token") != FirebaseInstanceId.getInstance().instanceId.await().token) {
                                logInPresenter.changeUserToken(uid)
                            }
                            if (user?.get("role") == "Administrador") {
                                val homeIntent = Intent(baseContext, HomeActivity::class.java)
                                homeIntent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(homeIntent)
                            } else {
                                val tvIntent = Intent(baseContext, TvActivity::class.java)
                                tvIntent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(tvIntent)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                showAlertLogIn()
                            }
                        }
                    }
                }
            } else {
                emptyEditText(emailTextInput)
                emptyEditText(passwordTextInput)
            }
        }

        resetPasswordButton.setOnClickListener {
            showResetPasswordDialog()
        }
    }

    override fun emptyEditText(text: TextInputLayout) {
        if(text.editText?.text.toString().trim().isEmpty()){
            text.error = getString(R.string.empty_field)
        } else { text.error = null }
    }

    override fun showAlertLogIn(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.login_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun showResetPasswordDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.activity_reset_password, null)
        builder.setView(view)
        builder.setTitle("Contraseña olvidada")
        builder.setMessage("Escribe tu email y te enviaremos un correo para reestablecer tu contraseña")
        builder.setPositiveButton("Aceptar") { _,_ ->
            val email = view.resetPasswordEditText.text.toString()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (logInPresenter.resetPassword(email)) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(baseContext, "Correo enviado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(baseContext, "Se ha producido un error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        builder.setNegativeButton("Cancelar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
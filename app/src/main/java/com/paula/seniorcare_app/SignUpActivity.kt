package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setup();
    }

    private fun setup(){
        title = "Registro"

        signUpButton.setOnClickListener() {
            if (name.editText?.text.toString().trim().isNotEmpty() && email.editText?.text.toString().trim().isNotEmpty() && password.editText?.text.toString().trim().isNotEmpty() && rol_menu.text.toString().trim().isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.editText?.text.toString(), password.editText?.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Guardado de datos
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", email.editText?.text.toString())
                        prefs.apply()

                        //Crear un usuario en la base de datos
                        db.collection("users").document(email.editText?.text.toString()).set(
                            hashMapOf(//"photo" to ¿?,
                                "name" to name.editText?.text.toString(),
                                "email" to email.editText?.text.toString(),
                                "rol" to rol_menu.text.toString(),
                                "provider" to "SeniorCare")
                        )
                        showHome()
                    } else {
                        showAlertSignUp()
                    }
                }
            } else {
                emptyEditText(name)
                emptyEditText(email)
                emptyEditText(password)

                if(rol_menu.text.toString().trim().isEmpty()){
                    dropdown_menu.error = getString(R.string.empty_field)
                } else { dropdown_menu.error = null }
            }
        }

        val roles = resources.getStringArray(R.array.rol_usuario)
        val adapter = ArrayAdapter(
            this,
            R.layout.lista_roles,
            roles
        )
        rol_menu.setAdapter(adapter)
    }

    private fun showAlertSignUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.signup_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this,HomeActivity::class.java)
        startActivity(homeIntent)
    }

    private fun emptyEditText(x: TextInputLayout) {
        if(x.editText?.text.toString().trim().isEmpty()){
            x.error = getString(R.string.empty_field)
        } else { x.error = null }
    }
}
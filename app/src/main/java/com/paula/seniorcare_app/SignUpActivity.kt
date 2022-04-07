package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setup();    //Configurar pantalla
    }

    private fun setup(){
        title = "Registro"
        signUpButton.setOnClickListener() {
            //FALTA COMPROBAR QUE LOS DATOS SEAN CORRECTOS
            if (name.editText?.text.toString().trim().isNotEmpty() && email.editText?.text.toString().trim().isNotEmpty() && password.editText?.text.toString().trim().isNotEmpty() && rol_menu.text.toString().trim().isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.editText?.text.toString(), password.editText?.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Guardado de datos
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("name", name.editText?.text.toString())
                        prefs.putString("email", email.editText?.text.toString())
                        prefs.putString("rol", rol_menu.text.toString())
                        prefs.apply()
                        //Ir a la pantalla Home
                        showHome()
                    } else {
                        showAlertSignUp()
                    }
                }
            } else {
                if(name.editText?.text.toString().trim().isEmpty()){
                    name.error = "El campo no puede estar vacío"
                } else { name.error = null }

                if (email.editText?.text.toString().trim().isEmpty()){
                    email.error = "El campo no puede estar vacío"
                } else { email.error = null }

                if (password.editText?.text.toString().trim().isEmpty()) {
                    password.error = "El campo no puede estar vacío"
                } else { password.error = null }

                if(rol_menu.text.toString().trim().isEmpty()){
                    dropdown_menu.error = "El campo no puede estar vacío"
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
        builder.setMessage("Se ha producido un error registrando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this,HomeActivity::class.java)
        startActivity(homeIntent)
    }
}
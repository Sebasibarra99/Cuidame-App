package com.sebastian.ecovibra1

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.databinding.ActivityRegistroEmailBinding

class RegistroEmailActivity : AppCompatActivity() {

    private lateinit var radioGroupRol: RadioGroup
    private lateinit var radioUsuario: RadioButton
    private lateinit var radioCuidador: RadioButton
    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //VARIABLES DE RADIO BUTTONS (CUIDADOR O ANCIANO)
        radioGroupRol = findViewById(R.id.radioGroupRol)
        radioUsuario = findViewById(R.id.radioUsuario)
        radioCuidador = findViewById(R.id.radioCuidador)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        binding. btnRegistrar.setOnClickListener {
            validarInformacion()
        }

    }


    private var nombres =""
    private var email = ""
    private var contrasena = ""
    private var r_password = ""
    private fun validarInformacion() {

        nombres = binding.etNombres.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        contrasena = binding.etPassword.text.toString().trim()
        r_password = binding.etRPassword.text.toString().trim()

        if (nombres.isEmpty()){
            binding.etNombres.error = "Ingrese nombre"
            binding.etNombres.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Correo invalido"
            binding.etEmail.requestFocus()
        }
        else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese correo"
            binding.etEmail.requestFocus()
        }
        else if (contrasena.isEmpty()){
            binding.etPassword.error = "Ingrese contrasena"
            binding.etPassword.requestFocus()
        }
        else if(r_password.isEmpty()){
            binding.etRPassword.error = "Repita contrasena"
            binding.etRPassword.requestFocus()

        }
        else if (contrasena != r_password){
            binding.etRPassword.error = "No coinciden las contrasenas"
            binding.etRPassword.requestFocus()
        }
        else if (!radioUsuario.isChecked && !radioCuidador.isChecked) {
            Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show()
        }

        else {
            registrarUsuario()
        }

        }



    private fun registrarUsuario() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,contrasena)
            .addOnSuccessListener {
                actualizarInformacion()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }



    private fun actualizarInformacion() {
        progressDialog.setMessage("Guardando Informacion")

        val uidU = firebaseAuth.uid
        val nombresU = nombres
        val emailU = firebaseAuth.currentUser!!.email
        val tiempoR = Constantes.obtenerTiempoD()

        val datosUsuario = HashMap<String, Any>()

        val rolSeleccionado = if (radioUsuario.isChecked) {
            "usuario"
        } else {
            "cuidador"
        }


        datosUsuario["uid"] = "$uidU"
        datosUsuario["nombres"] = "$nombresU"
        datosUsuario["email"] = "$emailU"
        datosUsuario["tiempoR"] = tiempoR
        datosUsuario["proveedor"] = "Email"
        datosUsuario["estado"] = "Online"
        datosUsuario["imagen"] = ""
        datosUsuario["rol"] = rolSeleccionado


        val reference = FirebaseDatabase.getInstance().getReference("usuarios")
        reference.child(uidU!!)
            .setValue(datosUsuario)
            .addOnSuccessListener {
                progressDialog.dismiss()

                if (rolSeleccionado == "usuario") {
                    startActivity(Intent(applicationContext, MainActivityUsuario::class.java))
                } else {
                    val intent = Intent(applicationContext,SeleccionarUsuarioActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                }
                finishAffinity()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }





}

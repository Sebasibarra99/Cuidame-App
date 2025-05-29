package com.sebastian.ecovibra1

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sebastian.ecovibra1.databinding.ActivityOlvidePasswordBinding

class OlvidePassword : AppCompatActivity() {

    private lateinit var binding : ActivityOlvidePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlvidePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEnviar.setOnClickListener {
            validarInformacion()
        }
    }

    private var email = ""
    private fun validarInformacion() {
        email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()){
            binding.etEmail.error = "Ingrese su email"
            binding.etEmail.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no valido"
            binding.etEmail.requestFocus()
        }
        else{
            enviarInstrucciones()
        }
    }

    private fun enviarInstrucciones() {
        progressDialog.setMessage("Enviando las instrucciones a $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Instrucciones enviadas",
                    Toast.LENGTH_SHORT
                ).show() // ← corrección aquí
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo el envio de instrucciones debido a: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show() // ← corrección aquí
            }
    }
}

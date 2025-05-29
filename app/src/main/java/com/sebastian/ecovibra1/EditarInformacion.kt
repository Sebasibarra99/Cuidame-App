package com.sebastian.ecovibra1

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sebastian.ecovibra1.databinding.ActivityEditarInformacionBinding

class EditarInformacion : AppCompatActivity() {

    private lateinit var  binding : ActivityEditarInformacionBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarInformacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnActualizar.setOnClickListener {
            validarInformacion()
        }


    }

    private var nombres = ""
    private fun validarInformacion() {
        nombres = binding.etNombres.text.toString().trim()

        if (nombres.isEmpty()){
            binding.etNombres.error = "Ingrese nombres"
            binding.etNombres.requestFocus()
        }else{
            actualizarInfo()
        }
    }

    /*
    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando informacion")
        progressDialog.show()

        val hashMap : HashMap<String, Any> = HashMap()

        hashMap["nombres"] = nombres

        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnFailureListener {
                progressDialog.dismiss()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Se actualizo su informacion",
                    Toast.LENGTH_SHORT
                ).show()

            }


    }*/
    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando información")
        progressDialog.show()

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["nombres"] = nombres

        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Se actualizó su información correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Error al actualizar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    binding.etNombres.setText(nombres)

                    try{
                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.ivPerfil)
                }catch (e :Exception){
                    Toast.makeText(
                        applicationContext,
                        "${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    }                }

                override fun onCancelled(error: DatabaseError) {

                }
            })







    }
}
package com.sebastian.ecovibra1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Adaptadores.AdaptadorUsuario
import com.sebastian.ecovibra1.Modelos.Usuario

class SeleccionarUsuarioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val listaUsuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_usuario)

        recyclerView = findViewById(R.id.recyclerUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        obtenerUsuariosDesdeFirebase()
    }

    private fun obtenerUsuariosDesdeFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("usuarios")

        ref.get().addOnSuccessListener { snapshot ->
            listaUsuarios.clear()

            for (userSnapshot in snapshot.children) {
                val usuario = userSnapshot.getValue(Usuario::class.java)
                if (usuario != null && usuario.rol == "usuario") {
                    listaUsuarios.add(usuario)
                }
            }

            recyclerView.adapter = AdaptadorUsuario(listaUsuarios) { usuarioSeleccionado ->
                guardarUidSeleccionado(usuarioSeleccionado.uid)
                vincularCuidadorConUsuario(usuarioSeleccionado.uid)

                Toast.makeText(this, "Usuario seleccionado: ${usuarioSeleccionado.nombres}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivityCuidador::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarUidSeleccionado(uid: String) {
        val prefs = getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("uidUsuarioSeleccionado", uid).apply()
    }

    private fun vincularCuidadorConUsuario(uidUsuario: String) {
        val uidCuidador = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)

        ref.child("usuarioVinculado").setValue(uidUsuario)
    }
}

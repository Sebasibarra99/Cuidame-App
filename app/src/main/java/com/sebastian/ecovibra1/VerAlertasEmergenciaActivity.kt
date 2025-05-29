package com.sebastian.ecovibra1

import com.sebastian.ecovibra1.Adaptadores.AdaptadorAlertaEmergencia
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sebastian.ecovibra1.Modelos.AlertaEmergencia

class VerAlertasEmergenciaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutVacio: LinearLayout
    private val listaAlertas = mutableListOf<AlertaEmergencia>()
    private var ultimaAlertaId: String? = null
    private lateinit var refAlertas: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_alertas_emergencia)

        recyclerView = findViewById(R.id.recyclerAlertas)
        layoutVacio = findViewById(R.id.layoutVacio)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        iniciarListenerDeAlertas()
    }

    private fun iniciarListenerDeAlertas() {
        val uidCuidador = FirebaseAuth.getInstance().currentUser?.uid ?: return

        refAlertas = FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("alertasEmergencia")

        refAlertas.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAlertas.clear()
                var alertaMasRecienteId: String? = null

                for (alertaSnap in snapshot.children) {
                    val alerta = alertaSnap.getValue(AlertaEmergencia::class.java)
                    if (alerta != null) {
                        listaAlertas.add(alerta)
                        alertaMasRecienteId = alertaSnap.key
                    }
                }

                if (listaAlertas.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    layoutVacio.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    layoutVacio.visibility = View.GONE
                    recyclerView.adapter = AdaptadorAlertaEmergencia(listaAlertas)

                    if (ultimaAlertaId == null) {
                        ultimaAlertaId = alertaMasRecienteId
                    } else if (alertaMasRecienteId != null && alertaMasRecienteId != ultimaAlertaId) {
                        ultimaAlertaId = alertaMasRecienteId
                        lanzarAlarmaEmergencia()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerAlertasEmergenciaActivity, "Error al cargar alertas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun lanzarAlarmaEmergencia() {
        Log.d("DEBUG", "Nueva alerta detectada. Abriendo pantalla de alarma.")
        val intent = Intent(this, AlarmaEmergenciaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}

package com.sebastian.ecovibra1

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Adaptadores.AdaptadorAlertaEmergencia
import com.sebastian.ecovibra1.Modelos.AlertaEmergencia

class AlertasEmergenciaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val listaAlertas = mutableListOf<AlertaEmergencia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alertas_emergencia)

        recyclerView = findViewById(R.id.recyclerAlertas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        cargarAlertas()
    }

    private fun cargarAlertas() {
        val uidCuidador = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("alertas_emergencia")
            .get()
            .addOnSuccessListener { snapshot ->
                listaAlertas.clear()
                for (alertaSnap in snapshot.children) {
                    val alerta = alertaSnap.getValue(AlertaEmergencia::class.java)
                    if (alerta != null) listaAlertas.add(alerta)
                }
                recyclerView.adapter = AdaptadorAlertaEmergencia(listaAlertas)
            }
    }
}

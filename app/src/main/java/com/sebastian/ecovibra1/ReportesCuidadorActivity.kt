package com.sebastian.ecovibra1

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Adaptadores.AdaptadorReporte
import com.sebastian.ecovibra1.Modelos.Reporte

class ReportesCuidadorActivity : AppCompatActivity() {

    private lateinit var layoutVacio: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private val listaReportes = mutableListOf<Reporte>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportes_cuidador)

        recyclerView = findViewById(R.id.recyclerReportes)
        layoutVacio = findViewById(R.id.layoutVacio)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        cargarReportes()
    }

    private fun cargarReportes() {
        val uidCuidador = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Obtener el UID del usuario vinculado desde SharedPreferences
        val prefs = getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
        val uidUsuarioVinculado = prefs.getString("uidUsuarioSeleccionado", null)

        if (uidUsuarioVinculado == null) {
            Toast.makeText(this, "No hay usuario vinculado", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("reportes")


        ref.get().addOnSuccessListener { snapshot ->
            listaReportes.clear()
            for (reporteSnap in snapshot.children) {
                val reporte = reporteSnap.getValue(Reporte::class.java)
                if (reporte != null) {
                    listaReportes.add(reporte)
                }
            }

            if (listaReportes.isEmpty()) {
                recyclerView.visibility = View.GONE
                layoutVacio.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                layoutVacio.visibility = View.GONE
                recyclerView.adapter = AdaptadorReporte(listaReportes)
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_SHORT).show()
        }
    }

}

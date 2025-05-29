package com.sebastian.ecovibra1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class VerUbicacionActivity : AppCompatActivity() {

    private var uidUsuarioSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ubicacion)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
        uidUsuarioSeleccionado = prefs.getString("uidUsuarioSeleccionado", null)

        if (uidUsuarioSeleccionado == null) {
            Toast.makeText(this, "No hay usuario vinculado", Toast.LENGTH_SHORT).show()
            return
        }

        cargarUbicacionDesdeFirebase()
    }

    private fun cargarUbicacionDesdeFirebase() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uidUsuarioSeleccionado!!)
            .child("ubicacion")

        ref.get().addOnSuccessListener { snapshot ->
            val lat = snapshot.child("latitud").getValue(Double::class.java)
            val lon = snapshot.child("longitud").getValue(Double::class.java)

            if (lat != null && lon != null) {
                abrirUbicacionEnGoogleMaps(lat, lon)
            } else {
                Toast.makeText(this, "No hay ubicación disponible", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirUbicacionEnGoogleMaps(lat: Double, lon: Double) {
        val uri = "geo:$lat,$lon?q=$lat,$lon(Ubicación del usuario)"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
        }
    }
}

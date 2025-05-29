package com.sebastian.ecovibra1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Fragmentos.FragmentEmergencia
import com.sebastian.ecovibra1.Fragmentos.FragmentSalud
import com.sebastian.ecovibra1.Fragmentos.FragmentUbicacion

class MainActivityUsuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_usuario)

        buscarCuidadorYGuardar()

        solicitarPermisoNotificaciones()
        crearCanalNotificaciones()

        // Cargar fragmento de salud por defecto
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentoFL, FragmentSalud())
            .commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNV)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_perfil -> {
                    loadFragment(FragmentSalud())
                    true
                }
                R.id.item_usuario -> {
                    loadFragment(FragmentUbicacion())
                    true
                }
                R.id.item_chat -> {
                    loadFragment(FragmentEmergencia())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentoFL, fragment)
            .commit()
    }

    private fun buscarCuidadorYGuardar() {
        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val prefs = getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)

        val dbRef = FirebaseDatabase.getInstance().getReference("cuidadores")

        dbRef.get().addOnSuccessListener { snapshot ->
            for (cuidadorSnap in snapshot.children) {
                val uidVinculado = cuidadorSnap.child("usuarioVinculado").value?.toString()
                if (uidVinculado == uidUsuario) {
                    val uidCuidador = cuidadorSnap.key
                    prefs.edit().putString("uidCuidador", uidCuidador).apply()
                    break
                }
            }
        }
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permiso), 1001)
            }
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "canal_recordatorios",
                "Recordatorios de medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            )
            canal.description = "Canal para notificaciones de medicamentos"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    // Opcional: manejo del permiso si quieres mostrar feedback
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No se podrán mostrar notificaciones", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

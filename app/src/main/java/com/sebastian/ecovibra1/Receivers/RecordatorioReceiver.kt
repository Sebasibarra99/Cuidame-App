package com.sebastian.ecovibra1  // 👈 o el paquete correcto según la ubicación

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nombre = intent.getStringExtra("nombre") ?: "Medicamento"
        val instrucciones = intent.getStringExtra("instrucciones") ?: ""

        // Lanzar la actividad de alarma en pantalla completa
        val pantallaAlarma = Intent(context, AlarmaActivity::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("instrucciones", instrucciones)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        context.startActivity(pantallaAlarma)
    }

}

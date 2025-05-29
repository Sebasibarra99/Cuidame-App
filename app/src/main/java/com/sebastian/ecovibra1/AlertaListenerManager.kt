package com.sebastian.ecovibra1

import android.content.Context
import android.content.Intent
import com.google.firebase.database.*

object AlertaListenerManager {
    private var ultimaAlertaId: String? = null

    fun iniciarListener(context: Context, uidCuidador: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("alertasEmergencia")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var alertaMasRecienteId: String? = null

                for (alertaSnap in snapshot.children) {
                    alertaMasRecienteId = alertaSnap.key
                }

                if (ultimaAlertaId == null) {
                    ultimaAlertaId = alertaMasRecienteId
                } else if (alertaMasRecienteId != null && alertaMasRecienteId != ultimaAlertaId) {
                    ultimaAlertaId = alertaMasRecienteId
                    val intent = Intent(context, AlarmaEmergenciaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

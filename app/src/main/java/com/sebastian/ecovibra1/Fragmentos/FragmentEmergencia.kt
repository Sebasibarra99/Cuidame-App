package com.sebastian.ecovibra1.Fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.R
import java.text.SimpleDateFormat
import java.util.*

class FragmentEmergencia : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_emergencia, container, false)

        val btnEmergencia = view.findViewById<Button>(R.id.btnEmergencia)

        btnEmergencia.setOnClickListener {
            enviarAlertaEmergencia()
        }

        return view
    }

    private fun enviarAlertaEmergencia() {
        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val prefs = requireContext().getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
        val uidCuidador = prefs.getString("uidCuidador", null) ?: return

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val hora = sdf.format(Date())

        val alerta = mapOf(
            "uidUsuario" to uidUsuario,
            "hora" to hora,
            "mensaje" to "🚨 ¡El usuario necesita ayuda!"
        )

        FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("alertasEmergencia") // ✅ Nodo corregido
            .push()
            .setValue(alerta)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Alerta enviada al cuidador", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al enviar alerta", Toast.LENGTH_SHORT).show()
            }
    }
}

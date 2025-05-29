package com.sebastian.ecovibra1.Fragmentos

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Adaptadores.AdaptadorMedicamento
import com.sebastian.ecovibra1.Modelos.Medicamento
import com.sebastian.ecovibra1.R
import com.sebastian.ecovibra1.RecordatorioReceiver
import java.util.Calendar

class FragmentSalud : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdaptadorMedicamento
    private val listaMedicamentos = mutableListOf<Medicamento>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_salud, container, false)

        // Referencia al RecyclerView
        recyclerView = view.findViewById(R.id.rvMedicamentos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Cargar medicamentos desde Firebase
        cargarMedicamentos()

        return view
    }

    private fun cargarMedicamentos() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val referencia = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("medicamentos")

        referencia.get().addOnSuccessListener { snapshot ->
            listaMedicamentos.clear()
            for (med in snapshot.children) {
                val medicamento = med.getValue(Medicamento::class.java)
                if (medicamento != null) {
                    listaMedicamentos.add(medicamento)

                    // 🔔 Programar la alarma automáticamente
                    programarAlarma(requireContext(), medicamento)
                }
            }

            adaptador = AdaptadorMedicamento(listaMedicamentos)
            recyclerView.adapter = adaptador

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar los medicamentos", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("ServiceCast")
    private fun programarAlarma(context: Context, medicamento: Medicamento) {
        val hora = medicamento.hora.split(":")
        if (hora.size != 2) return
        val horaInt = hora[0].toInt()
        val minutoInt = hora[1].toInt()

        val intent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra("nombre", medicamento.nombre)
            putExtra("instrucciones", medicamento.instrucciones)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamento.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, horaInt)
            set(Calendar.MINUTE, minutoInt)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1) // Si la hora ya pasó, programa para mañana
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendario.timeInMillis,
            pendingIntent
        )
    }

}

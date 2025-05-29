package com.sebastian.ecovibra1.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.Modelos.Medicamento
import com.sebastian.ecovibra1.R
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorMedicamento(private val lista: MutableList<Medicamento>) :
    RecyclerView.Adapter<AdaptadorMedicamento.MedicamentoViewHolder>() {

    class MedicamentoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoto: ImageView = view.findViewById(R.id.ivFotoMedicamento)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvInstrucciones: TextView = view.findViewById(R.id.tvInstrucciones)
        val btnTomado: Button = view.findViewById(R.id.btnTomado)
        val btnNoTomado: Button = view.findViewById(R.id.btnNoTomado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicamento, parent, false)
        return MedicamentoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        val medicamento = lista[position]
        val context = holder.itemView.context
        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return

        holder.tvNombre.text = medicamento.nombre
        holder.tvHora.text = "Hora: ${medicamento.hora}"
        holder.tvInstrucciones.text = "Instrucciones: ${medicamento.instrucciones}"

        Glide.with(context)
            .load(medicamento.foto)
            .placeholder(R.drawable.placeholder_medicamento)
            .into(holder.ivFoto)

        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uidUsuario)
            .child("medicamentos")
            .child(medicamento.id)

        holder.btnTomado.setOnClickListener {
            ref.child("tomado").setValue(true)
                .addOnSuccessListener {
                    enviarReporteACuidador(context, medicamento)
                    lista.removeAt(position)
                    notifyItemRemoved(position)
                }
        }

        holder.btnNoTomado.setOnClickListener {
            ref.child("tomado").setValue(false)
                .addOnSuccessListener {
                    Toast.makeText(context, "Medicamento marcado como no tomado", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = lista.size

    private fun enviarReporteACuidador(context: Context, medicamento: Medicamento) {
        val prefs = context.getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val uidCuidador = prefs.getString("uidCuidador", null) ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("cuidadores")
            .child(uidCuidador)
            .child("reportes")
            .push()

        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val datos = mapOf(
            "medicamento" to medicamento.nombre,
            "hora" to medicamento.hora,
            "fecha" to fecha,
            "mensaje" to "El usuario tomó su medicamento",
            "uidUsuario" to uidUsuario
        )

        ref.setValue(datos)
    }
}

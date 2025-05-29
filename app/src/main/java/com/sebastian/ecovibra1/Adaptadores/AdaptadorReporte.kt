package com.sebastian.ecovibra1.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sebastian.ecovibra1.Modelos.Reporte
import com.sebastian.ecovibra1.R

class AdaptadorReporte(private val lista: List<Reporte>) :
    RecyclerView.Adapter<AdaptadorReporte.ReporteViewHolder>() {

    class ReporteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMedicamento: TextView = view.findViewById(R.id.tvMedicamento)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte, parent, false)
        return ReporteViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
        val reporte = lista[position]
        holder.tvMedicamento.text = "Medicamento: ${reporte.medicamento}"
        holder.tvHora.text = "Hora: ${reporte.hora}"
        holder.tvFecha.text = "Fecha: ${reporte.fecha}"
        holder.tvMensaje.text = reporte.mensaje
    }

    override fun getItemCount(): Int = lista.size
}

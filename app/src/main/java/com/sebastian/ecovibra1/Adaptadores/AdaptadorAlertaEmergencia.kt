package com.sebastian.ecovibra1.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sebastian.ecovibra1.Modelos.AlertaEmergencia
import com.sebastian.ecovibra1.R

class AdaptadorAlertaEmergencia(private val lista: List<AlertaEmergencia>) :
    RecyclerView.Adapter<AdaptadorAlertaEmergencia.EmergenciaViewHolder>() {

    class EmergenciaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvAlertaFecha)
        val tvHora: TextView = view.findViewById(R.id.tvAlertaHora)
        val tvMensaje: TextView = view.findViewById(R.id.tvAlertaMensaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergenciaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta_emergencia, parent, false)
        return EmergenciaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: EmergenciaViewHolder, position: Int) {
        val alerta = lista[position]
        holder.tvFecha.text = "Fecha: ${alerta.fecha}"
        holder.tvHora.text = "Hora: ${alerta.hora}"
        holder.tvMensaje.text = alerta.mensaje
    }

    override fun getItemCount(): Int = lista.size
}

package com.sebastian.ecovibra1.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sebastian.ecovibra1.Modelos.Usuario
import com.sebastian.ecovibra1.R

class AdaptadorUsuario(
    private val listaUsuarios: List<Usuario>,
    private val onItemClick: (Usuario) -> Unit
) : RecyclerView.Adapter<AdaptadorUsuario.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)

        fun bind(usuario: Usuario) {
            tvNombre.text = usuario.nombres
            tvEmail.text = usuario.email

            itemView.setOnClickListener {
                onItemClick(usuario)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(listaUsuarios[position])
    }

    override fun getItemCount(): Int = listaUsuarios.size
}

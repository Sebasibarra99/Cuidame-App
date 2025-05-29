package com.sebastian.ecovibra1.adaptadores // Recomendación: nombre de paquete en minúsculas

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sebastian.ecovibra1.Chat.ChatActivity
import com.sebastian.ecovibra1.Constantes
import com.sebastian.ecovibra1.Modelos.Chats
import com.sebastian.ecovibra1.R
import com.sebastian.ecovibra1.databinding.ItemChatsBinding

class AdaptadorChats(
    private val context: Context,
    private val chatArrayList: ArrayList<Chats>
) : RecyclerView.Adapter<AdaptadorChats.HolderChats>() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val miUid: String = firebaseAuth.uid!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChats {
        val binding = ItemChatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderChats(binding)
    }

    override fun getItemCount(): Int {
        return chatArrayList.size
    }

    override fun onBindViewHolder(holder: HolderChats, position: Int) {
        val modeloChats = chatArrayList[position]

        cargarUltimoMensaje(modeloChats, holder)

        holder.itemView.setOnClickListener {
            val uidRecibimos = modeloChats.uidRecibimos
            if (uidRecibimos.isNotEmpty()) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("uid", uidRecibimos)
                context.startActivity(intent)
            }
        }
    }

    private fun cargarUltimoMensaje(modeloChats: Chats, holder: HolderChats) {
        val chatKey = modeloChats.keyChat

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val emisorUid = "${ds.child("emisorUid").value}"
                        val idMensaje = "${ds.child("idMensaje").value}"
                        val mensaje = "${ds.child("mensaje").value}"
                        val receptorUid = "${ds.child("receptorUid").value}"
                        val tiempo = ds.child("tiempo").value as Long
                        val tipoMensaje = "${ds.child("tipoMensaje").value}"

                        val formatoFechaHora = Constantes.obtenerFechaHora(tiempo)

                        modeloChats.emisorUid = emisorUid
                        modeloChats.idMensaje = idMensaje
                        modeloChats.mensaje = mensaje
                        modeloChats.receptorUid = receptorUid
                        modeloChats.tipoMensaje = tipoMensaje
                        modeloChats.tiempo = tiempo

                        holder.tvFecha.text = formatoFechaHora

                        holder.tvUltimoMensaje.text = if (tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO) {
                            mensaje
                        } else {
                            "Se ha enviado una imagen"
                        }

                        cargarInfoUsuarioRecibido(modeloChats, holder)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de errores si lo deseas
                }
            })
    }

    private fun cargarInfoUsuarioRecibido(modeloChats: Chats, holder: HolderChats) {
        val emisorUid = modeloChats.emisorUid
        val receptorUid = modeloChats.receptorUid

        val uidRecibimos = if (emisorUid == miUid) receptorUid else emisorUid
        modeloChats.uidRecibimos = uidRecibimos

        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child(uidRecibimos)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    modeloChats.nombres = nombres
                    modeloChats.imagen = imagen

                    holder.tvNombres.text = nombres
                    try {
                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(holder.ivPerfil)
                    } catch (e: Exception) {
                        // Manejo opcional
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de errores si lo deseas
                }
            })
    }

    inner class HolderChats(binding: ItemChatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivPerfil = binding.IvPerfil
        val tvNombres = binding.tvNombres
        val tvUltimoMensaje = binding.tvUltimoMensaje
        val tvFecha = binding.tvFecha
    }
}

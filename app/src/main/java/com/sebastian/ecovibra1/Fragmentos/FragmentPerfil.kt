package com.sebastian.ecovibra1.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sebastian.ecovibra1.CambiarPassword
import com.sebastian.ecovibra1.Constantes
import com.sebastian.ecovibra1.EditarInformacion
import com.sebastian.ecovibra1.OpcionesLoginActivity
import com.sebastian.ecovibra1.R
import com.sebastian.ecovibra1.databinding.FragmentPerfilBinding


class FragmentPerfil : Fragment() {

    private lateinit var binding : FragmentPerfilBinding
    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPerfilBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        cargarInformacion()


        binding.btnActualizarInfo.setOnClickListener {
            startActivity(Intent(mContext, EditarInformacion::class.java))
        }


        binding.btnCambiarPass.setOnClickListener {
            startActivity(Intent(mContext,CambiarPassword::class.java))
        }



        binding.btnCerarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent( mContext,OpcionesLoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"
                    var t_registro = "${snapshot.child("tiempoR").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    if (t_registro == "null"){
                        t_registro = "0"
                    }

                    //conversion a fecha
                    val fecha = Constantes.formatoFecha(t_registro.toLong())

                    //Setear informacion en las vistas
                    binding.tvNombres.text = nombres
                    binding.tvEmail.text = email
                    binding.tvProveedor.text = proveedor
                    binding.tvTRegistro.text = fecha

                    //setear la imagen en el textView
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(binding.ivPerfil)
                    }catch(e:Exception){
                        Toast.makeText(
                            mContext,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (proveedor == "Email"){
                        binding.btnCambiarPass.visibility = View.VISIBLE
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}
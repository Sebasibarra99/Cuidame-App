package com.sebastian.ecovibra1.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sebastian.ecovibra1.Adaptadores.AdaptadorUsuario
import com.sebastian.ecovibra1.Modelos.Usuario
import com.sebastian.ecovibra1.databinding.FragmentUsuariosBinding

class FragmentUsuarios : Fragment() {

    private lateinit var binding: FragmentUsuariosBinding
    private lateinit var mContext: Context
    private var usuarioAdaptador: AdaptadorUsuario? = null
    private var usuarioLista: MutableList<Usuario> = mutableListOf()

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false)

        binding.RVUsuarios.setHasFixedSize(true)
        binding.RVUsuarios.layoutManager = LinearLayoutManager(mContext)

        binding.etBuscarUsuario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                buscarUsuario(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        listarUsuarios()

        return binding.root
    }

    private fun listarUsuarios() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val reference = FirebaseDatabase.getInstance().reference.child("usuarios").orderByChild("nombres")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuarioLista.clear()

                if (binding.etBuscarUsuario.text.toString().isEmpty()) {
                    for (sn in snapshot.children) {
                        val usuario = sn.getValue(Usuario::class.java)
                        if (usuario != null && usuario.uid != firebaseUser) {
                            usuarioLista.add(usuario)
                        }
                    }

                    usuarioAdaptador = AdaptadorUsuario(usuarioLista) { /* click vacío */ }
                    binding.RVUsuarios.adapter = usuarioAdaptador
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun buscarUsuario(usuario: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val reference = FirebaseDatabase.getInstance().reference.child("usuarios")
            .orderByChild("nombres")
            .startAt(usuario.lowercase())
            .endAt(usuario.lowercase() + "\uf8ff")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuarioLista.clear()

                for (ss in snapshot.children) {
                    val usuario = ss.getValue(Usuario::class.java)
                    if (usuario != null && usuario.uid != firebaseUser) {
                        usuarioLista.add(usuario)
                    }
                }

                usuarioAdaptador = AdaptadorUsuario(usuarioLista) { /* click vacío */ }
                binding.RVUsuarios.adapter = usuarioAdaptador
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

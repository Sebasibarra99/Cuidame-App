package com.sebastian.ecovibra1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sebastian.ecovibra1.Fragmentos.FragmentChats
import com.sebastian.ecovibra1.Fragmentos.FragmentPerfil
import com.sebastian.ecovibra1.Fragmentos.FragmentUsuarios
import com.sebastian.ecovibra1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null){
            irOpcionesLogin()
        }

        // Fragmento por defecto al iniciar la actividad
        verFragmentoPerfil()

        binding.bottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_perfil -> {
                    verFragmentoPerfil()
                    true
                }
                R.id.item_usuario -> {
                    verFragmentoUsuarios()
                    true
                }
                R.id.item_chat -> {
                    verFragmentoChats()
                    true
                }
                else -> false
            }
        }
    }

    private fun irOpcionesLogin() {
        startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
        finishAffinity()
    }

    private fun verFragmentoPerfil() {
        binding.tvTitulo.text = "PERFIL" // Actualiza el título si tienes un TextView
        val fragment = FragmentPerfil()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment,"Fragment perfil")
        fragmentTransaction.commit()
    }

    private fun verFragmentoUsuarios() {
        binding.tvTitulo.text = "USUARIOS"
        val fragment = FragmentUsuarios()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment,"fragment usuarios")
        fragmentTransaction.commit()
    }

    private fun verFragmentoChats() {
        binding.tvTitulo.text = "CHATS"
        val fragment = FragmentChats()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment,"fragment chats")
        fragmentTransaction.commit()
    }
}

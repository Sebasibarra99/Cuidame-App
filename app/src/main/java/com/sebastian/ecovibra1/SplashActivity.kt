package com.sebastian.ecovibra1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        logo = findViewById(R.id.logoSplash)

        // Cargar y aplicar la animación
        val animacion = AnimationUtils.loadAnimation(this, R.animator.animacion_splash)
        logo.startAnimation(animacion)

        // Esperar a que termine la animación (2.5 seg) y verificar sesión
        Handler(Looper.getMainLooper()).postDelayed({
            val usuarioActual = firebaseAuth.currentUser
            if (usuarioActual != null) {
                val uid = usuarioActual.uid
                val reference = FirebaseDatabase.getInstance().getReference("usuarios")

                reference.child(uid).get().addOnSuccessListener { snapshot ->
                    val rol = snapshot.child("rol").value.toString()

                    when (rol) {
                        "usuario" -> startActivity(Intent(this, MainActivityUsuario::class.java))
                        "cuidador" -> startActivity(Intent(this, MainActivityCuidador::class.java))
                        else -> {
                            Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, OpcionesLoginActivity::class.java))
                        }
                    }
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al obtener el rol", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OpcionesLoginActivity::class.java))
                    finish()
                }

            } else {
                startActivity(Intent(this, OpcionesLoginActivity::class.java))
                finish()
            }
        }, 2500) // Tiempo para que se vea la animación
    }
}

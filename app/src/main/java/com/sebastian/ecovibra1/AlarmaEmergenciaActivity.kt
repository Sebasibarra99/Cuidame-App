package com.sebastian.ecovibra1

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AlarmaEmergenciaActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    @SuppressLint("MissingPermission", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d("ALARMA", "Paso 1: Entrando a onCreate")

            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            Log.d("ALARMA", "Paso 2: Configurada pantalla completa")

            setContentView(R.layout.activity_alarma_emergencia)
            Log.d("ALARMA", "Paso 3: Layout cargado correctamente")

            val mensaje = findViewById<TextView>(R.id.tvMensajeAlarma)
            val btnDetener = findViewById<Button>(R.id.btnDetenerAlarma)
            Log.d("ALARMA", "Paso 4: Views encontradas")

            mensaje.text = "🚨 ¡Emergencia! El usuario necesita ayuda."

            mediaPlayer = MediaPlayer.create(this, R.raw.sonido_alarma)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
            Log.d("ALARMA", "Paso 5: Sonido iniciado")

            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            Log.d("ALARMA", "Paso 6: Vibración activada")

            btnDetener.setOnClickListener {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                finish()
            }

        } catch (e: Exception) {
            Log.e("ALARMA", "❌ Error exacto: ", e)
            Toast.makeText(this, "Error al mostrar alarma", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}

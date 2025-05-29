package com.sebastian.ecovibra1

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmaActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar sobre pantalla de bloqueo
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_alarma)

        val nombre = intent.getStringExtra("nombre") ?: "Medicamento"
        val instrucciones = intent.getStringExtra("instrucciones") ?: ""

        findViewById<TextView>(R.id.tvNombreAlarma).text = nombre
        findViewById<TextView>(R.id.tvInstruccionesAlarma).text = instrucciones

        // Sonido de alarma
        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_alarma)
        mediaPlayer.isLooping = true
        mediaPlayer.start()


        // Botón para detener
        findViewById<Button>(R.id.btnTomar).setOnClickListener {
            mediaPlayer.stop()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
}

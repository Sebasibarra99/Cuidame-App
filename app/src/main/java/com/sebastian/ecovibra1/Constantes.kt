package com.sebastian.ecovibra1

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Locale

object Constantes {

    const val MENSAJE_TIPO_TEXTO = "TEXTO"
    const val MENSAJE_TIPO_IMAGEN = "IMAGEN"

    fun obtenerTiempoD(): Long {
        return System.currentTimeMillis()
    }

    fun formatoFecha(tiempo: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }

    fun obtenerFechaHora(tiempo : Long) : String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:a", Locale.ENGLISH)
        return dateFormat.format(calendar.time)

    }

    fun rutaChat(receptorUid : String , emisorUid : String) : String{
        val arrayUid = arrayOf(receptorUid,emisorUid)
        Arrays.sort(arrayUid)
        return "${arrayUid[0]}_${arrayUid[1]}"
    }
}

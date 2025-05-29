package com.sebastian.ecovibra1.Fragmentos

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sebastian.ecovibra1.R

class FragmentUbicacion : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ubicacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Pedir permisos si no se han concedido
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarUbicacion)
        btnEnviar.setOnClickListener {
            obtenerYEnviarUbicacion()
        }
    }

    private fun obtenerYEnviarUbicacion() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    val datosUbicacion = mapOf(
                        "latitud" to lat,
                        "longitud" to lon,
                        "hora" to System.currentTimeMillis()
                    )

                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener

                    FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("ubicacion")
                        .setValue(datosUbicacion)

                    Toast.makeText(requireContext(), "Ubicación enviada al cuidador", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

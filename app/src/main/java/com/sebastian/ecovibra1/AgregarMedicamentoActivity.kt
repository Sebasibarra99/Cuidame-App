package com.sebastian.ecovibra1

import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AgregarMedicamentoActivity : AppCompatActivity() {

    private lateinit var seleccionImagenLauncher: ActivityResultLauncher<Intent>
    private var uriFotoSeleccionada: Uri? = null

    private lateinit var etNombre: EditText
    private lateinit var tvHora: TextView
    private lateinit var etInstrucciones: EditText
    private lateinit var btnGuardar: Button
    private lateinit var ivFoto: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var horaSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_medicamento)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Subiendo medicamento...")
        progressDialog.setCancelable(false)

        etNombre = findViewById(R.id.etNombreMedicamento)
        tvHora = findViewById(R.id.tvHora)
        etInstrucciones = findViewById(R.id.etInstrucciones)
        btnGuardar = findViewById(R.id.btnGuardar)
        ivFoto = findViewById(R.id.ivFoto)

        // Registrar launcher para seleccionar imagen
        seleccionImagenLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uriFotoSeleccionada = data?.data
                ivFoto.setImageURI(uriFotoSeleccionada)
            }
        }

        // Al hacer clic en la imagen, abrir galería
        ivFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            seleccionImagenLauncher.launch(intent)
        }

        // Selector de hora
        tvHora.setOnClickListener {
            val calendario = Calendar.getInstance()
            val hora = calendario.get(Calendar.HOUR_OF_DAY)
            val minuto = calendario.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                { _, horaInt, minutoInt ->
                    horaSeleccionada = String.format("%02d:%02d", horaInt, minutoInt)
                    tvHora.text = "Hora: $horaSeleccionada"
                },
                hora, minuto, true)

            timePickerDialog.show()
        }

        // Botón Guardar
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val instrucciones = etInstrucciones.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = "Ingrese el nombre del medicamento"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            if (horaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("cuidame_prefs", Context.MODE_PRIVATE)
            val uidUsuario = prefs.getString("uidUsuarioSeleccionado", null)

            if (uidUsuario == null) {
                Toast.makeText(this, "Primero seleccione un usuario", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idMedicamento = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uidUsuario)
                .child("medicamentos")
                .push()
                .key

            if (idMedicamento == null) {
                Toast.makeText(this, "Error al generar ID del medicamento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.setMessage("Subiendo medicamento...")
            progressDialog.show()

            if (uriFotoSeleccionada != null) {
                val storageRef = FirebaseStorage.getInstance()
                    .getReference("medicamentos/$uidUsuario/$idMedicamento.jpg")

                storageRef.putFile(uriFotoSeleccionada!!)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception ?: Exception("Error desconocido al subir imagen")
                        }
                        storageRef.downloadUrl
                    }
                    .addOnSuccessListener { uri ->
                        guardarMedicamentoEnFirebase(
                            uidUsuario,
                            idMedicamento,
                            nombre,
                            horaSeleccionada,
                            instrucciones,
                            uri.toString()
                        )
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error al subir imagen: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

            } else {
                guardarMedicamentoEnFirebase(
                    uidUsuario,
                    idMedicamento,
                    nombre,
                    horaSeleccionada,
                    instrucciones,
                    ""
                )
            }
        }

    }

    private fun guardarMedicamentoEnFirebase(
        uidUsuario: String,
        idMedicamento: String,
        nombre: String,
        hora: String,
        instrucciones: String,
        urlFoto: String
    ) {
        val medicamento = hashMapOf(
            "id" to idMedicamento,
            "nombre" to nombre,
            "hora" to hora,
            "instrucciones" to instrucciones,
            "foto" to urlFoto
        )

        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uidUsuario)
            .child("medicamentos")
            .child(idMedicamento)
            .setValue(medicamento)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Medicamento guardado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Error al guardar medicamento", Toast.LENGTH_SHORT).show()
            }
    }
}

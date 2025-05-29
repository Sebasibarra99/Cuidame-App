package com.sebastian.ecovibra1.Chat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sebastian.ecovibra1.Adaptadores.AdaptadorChat
import com.sebastian.ecovibra1.Constantes
import com.sebastian.ecovibra1.Modelos.Chat
import com.sebastian.ecovibra1.R
import com.sebastian.ecovibra1.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityChatBinding
    private var uid = ""

    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var miUid = ""


    private var chatRuta =""
    private var imagenUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        uid = intent.getStringExtra("uid")!!
        miUid = firebaseAuth.uid!!

        chatRuta = Constantes.rutaChat(uid,miUid)

        binding.adjuntarFAB.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                imagenGaleria()
            }else{
                solicitarPermisoDeAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.IBRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.enviarFAB.setOnClickListener {
            validarMensaje()
        }

        cargarInfo()
        cargarMensajes()
    }

    private fun cargarMensajes() {
        val mensajesArrayList = ArrayList<Chat>()
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatRuta)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mensajesArrayList.clear()
                    for (ds : DataSnapshot in snapshot.children){
                        try {
                            val chat = ds.getValue(Chat::class.java)
                            mensajesArrayList.add(chat!!)

                        }catch (e:Exception){

                        }
                    }

                    val adaptadorChat = AdaptadorChat(this@ChatActivity, mensajesArrayList)
                    binding.chatsRV.adapter = adaptadorChat
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun validarMensaje() {
        val mensaje = binding.EtMensajeChats.text.toString().trim()
        val tiempo = Constantes.obtenerTiempoD()

        if (mensaje.isEmpty()){
            Toast.makeText(this, "Ingrese un mensaje", Toast.LENGTH_SHORT).show()
        }else{
            enviarMensaje(Constantes.MENSAJE_TIPO_TEXTO, mensaje, tiempo)
        }
    }



    private fun cargarInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("usuarios")
        ref.child(uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen  = "${snapshot.child("imagen").value}"

                    binding.txtNombreUsuario.text = nombres

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun imagenGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadosGaleriaARL.launch(intent)
    }

    private val resultadosGaleriaARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                subirImgStorage()
            }else{
                Toast.makeText(
                    this,
                    "cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private val solicitarPermisoDeAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){esConcedido->
            if(esConcedido){
                imagenGaleria()
            }else{
                Toast.makeText(
                    this,
                    "el permiso de almacenamiento ha sido concedido",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    private fun subirImgStorage(){
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoD()
        val nombreRutaImg = "ImagenesChat/$tiempo"
        val storageRef = FirebaseStorage.getInstance().getReference(nombreRutaImg)
        storageRef.putFile(imagenUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagen = uriTask.result.toString()
                if (uriTask.isSuccessful){
                    enviarMensaje(Constantes.MENSAJE_TIPO_IMAGEN, urlImagen, tiempo)
                }

            }.addOnFailureListener {e->
                Toast.makeText(this,
                    "No se pudo enviar la imagen debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

    }

    private fun enviarMensaje(tipoMensaje: String, mensaje: String, tiempo: Long) {

        progressDialog.setMessage("Enviando mensaje")
        progressDialog.show()

        val refChat = FirebaseDatabase.getInstance().getReference("Chats")
        val KeyId = "${refChat.push().key}"
        val hashMap = HashMap<String, Any>()

        hashMap["idMensaje"] = "${KeyId}"
        hashMap["tipoMensaje"] = "${tipoMensaje}"
        hashMap["mensaje"] = "${mensaje}"
        hashMap["emisorUid"] = "${miUid}"
        hashMap["receptorUid"] = "$uid"
        hashMap["tiempo"] = tiempo

        refChat.child(chatRuta)
            .child(KeyId)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.EtMensajeChats.setText("")

            }.addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se pudo enviar el mensaje debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()

            }



    }


}
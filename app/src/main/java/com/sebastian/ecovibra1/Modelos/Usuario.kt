package com.sebastian.ecovibra1.Modelos

class Usuario {
    var uid: String = ""
    var email: String = ""
    var nombres: String = ""
    var imagen: String = ""
    var rol: String = ""  // ➕ Agregado para filtrar por tipo de usuario

    constructor()
    constructor(uid: String, email: String, nombres: String, imagen: String, rol: String) {
        this.uid = uid
        this.email = email
        this.nombres = nombres
        this.imagen = imagen
        this.rol = rol
    }
}

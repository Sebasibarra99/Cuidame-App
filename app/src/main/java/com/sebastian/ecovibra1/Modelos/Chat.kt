package com.sebastian.ecovibra1.Modelos

class Chat {
    var idMensaje : String = ""
    var tipoMensaje : String = ""
    var mensaje : String = ""
    var emisorUid : String = ""
    var tiempo : Long = 0

    constructor()

    constructor(
        idMensaje: String,
        tipoMensaje: String,
        emisorUid: String,
        mensaje: String,
        tiempo: Long
    ) {
        this.idMensaje = idMensaje
        this.tipoMensaje = tipoMensaje
        this.emisorUid = emisorUid
        this.mensaje = mensaje
        this.tiempo = tiempo
    }


}
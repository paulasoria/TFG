package com.paula.seniorcare_app.model

import java.io.Serializable

data class History(
    var id: String? = null,
    var time: String? = null,       //Alert o Videocall
    var date: String? = null,       //Alert o Videocall
    var tag: String? = null,        //Alert: Etiqueta descriptiva
    var state: String? = null,      //Videocall: Estado de la llamada (in progress, accepted, rejected or lost)
    var duration: Int? = null       //Videocall: Tiempo llamada
) : Serializable
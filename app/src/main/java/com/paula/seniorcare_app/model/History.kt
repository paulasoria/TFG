package com.paula.seniorcare_app.model

import java.io.Serializable

data class History(
    var id: String? = null,
    var receiver : String? = null,
    var time: String? = null,       //Alert o Videocall
    var date: String? = null,       //Alert o Videocall
    var tag: String? = null        //Alert: Etiqueta descriptiva o Videocall: Estado de la llamada (waiting, accepted, rejected or lost)
) : Serializable
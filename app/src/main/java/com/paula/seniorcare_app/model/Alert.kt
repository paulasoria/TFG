package com.paula.seniorcare_app.model

import java.io.Serializable

data class Alert(
    var id: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var tag: String? = null,
    var repetition: String? = null, //weekly or eventually
    var hour: Int,
    var minute: Int,
    var dayOfWeek: String? = null, //weekly         TYPE DAYOFWEEK¿?
    var date: String? = null, //event
    var isOn: Boolean   //Alarma activada o desactivada
) : Serializable
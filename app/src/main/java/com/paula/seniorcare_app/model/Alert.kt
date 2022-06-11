package com.paula.seniorcare_app.model

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Alert(
    var id: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var receiverName : String? = null,
    var receiverEmail : String? = null,
    var tag: String? = null,
    var repetition: String? = null, //weekly or eventually
    var time: String? = null,       //weekly or eventually
    var daysOfWeek: HashMap<String,Int>?, //weekly    1 on, 0 off
    var date: String? = null                 //event     y,m,d
) : Serializable
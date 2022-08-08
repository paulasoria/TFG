package com.paula.seniorcare_app.dataclass

import java.io.Serializable

data class Alert(
    var id: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var receiverName : String? = null,
    var receiverEmail : String? = null,
    var tag: String? = null,
    var repetition: String? = null,
    var time: String? = null,
    var daysOfWeek: HashMap<String,Int>?,
    var date: String? = null
) : Serializable
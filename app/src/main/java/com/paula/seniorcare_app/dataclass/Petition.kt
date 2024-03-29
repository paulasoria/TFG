package com.paula.seniorcare_app.dataclass

import java.io.Serializable

data class Petition (
    var id: String? = null,
    var sender: String? = null,
    var senderName : String? = null,
    var senderEmail : String? = null,
    var senderImage : String? = null,
    var senderRole : String? = null,
    var receiver: String? = null,
    var state: String? = null
) : Serializable
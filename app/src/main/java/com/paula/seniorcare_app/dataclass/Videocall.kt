package com.paula.seniorcare_app.dataclass

import java.io.Serializable

data class Videocall(
    var id: String? = null,
    var sender: String? = null,
    var senderName: String? = null,
    var receiver: String? = null,
    var receiverName: String? = null,
    var date: String? = null,
    var time: String? = null,
    var state: String? = null
) : Serializable
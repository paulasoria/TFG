package com.paula.seniorcare_app.model

import java.io.Serializable

data class Videocall(
    var id: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var duration: String? = null,
    var date: String? = null,
    var hour: String? = null,
    var state: String? = null //in progress, accepted, rejected or lost
) : Serializable
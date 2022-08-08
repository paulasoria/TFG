package com.paula.seniorcare_app.dataclass

import java.io.Serializable

data class User(
    var uid: String? = null,
    var token: String? = null,
    var image : String? = null,
    var name: String? = null,
    var email: String? = null,
    var role: String? = null
) : Serializable

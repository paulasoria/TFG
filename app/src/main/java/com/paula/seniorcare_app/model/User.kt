package com.paula.seniorcare_app.model

import java.io.Serializable

data class User(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var role: String? = null,
    var image : String? = null,
    var relatives: List<User>? = null
) : Serializable

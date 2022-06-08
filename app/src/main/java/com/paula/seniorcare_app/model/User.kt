package com.paula.seniorcare_app.model

import java.io.Serializable

data class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var role: String? = null,
    var image : String? = null,
    var relatives: ArrayList<String>? = null
) : Serializable

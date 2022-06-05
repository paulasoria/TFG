package com.paula.seniorcare_app.model

import java.io.Serializable

data class Petition (
    var id: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var state: String? = null   //pending, accepted or rejected
) : Serializable
package com.paula.seniorcare_app.dataclass

import java.io.Serializable

data class History(
    var id: String? = null,
    var receiver : String? = null,
    var time: String? = null,
    var date: String? = null,
    var tag: String? = null
) : Serializable
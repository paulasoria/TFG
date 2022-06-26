package com.paula.seniorcare_app.model

import java.io.Serializable

data class Videocall(
    var id: String? = null,
    var sender: String? = null,
    var senderName: String? = null,
    var receiver: String? = null,
    var receiverName: String? = null,
    var date: String? = null,
    var time: String? = null,
    var state: String? = null //waiting, accepted, rejected, lost or finished
) : Serializable

//LLAMO Y NO ME LO COGEN
//  YO: waiting - lost              -> Llamada saliente/realizada
//  FAMILIAR: waiting - lost        -> Llamada perdida

//LLAMO Y SÍ ME LO COGEN
//  YO: waiting - accepted          -> Llamada saliente/realizada
//  FAMILIAR: waiting - accepted    -> Llamada entrante

//LLAMO Y ME CUELGAN
//  YO: waiting - rejected          -> Llamada saliente/realizada
//  FAMILIAR: waiting - rejected    -> Llamada rechazada

//SI SENDER=YO, DA IGUAL EL ESTADO, SE PONE COMO LLAMADA SALIENTE/REALIZADA
//SI RECEIVER=YO, SE PONE COMO PERDIDA, ENTRANTE O RECHAZADA
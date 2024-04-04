package com.ehsannarmani.socketehsan.utils

import java.text.SimpleDateFormat

fun String.isDateTime():Boolean{
    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    return try {
        format.parse(this.replace("T"," ").replace("Z",""))
        true
    }catch (e:Exception){
        println(e.message)
        false
    }
}
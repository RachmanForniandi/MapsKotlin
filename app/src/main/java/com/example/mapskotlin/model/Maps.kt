package com.example.mapskotlin.model

class Maps {

    var key : String?=null
    var name : String?=null
    var owner : String?=null
    var latitude : String?=null
    var longitude : String?=null

    fun toMap():HashMap<String,Any>{

        val update = HashMap<String,Any>()
        update["name"] = name.toString()
        update["owner"] = owner.toString()
        update["latitude"] = latitude.toString()
        update["longitude"] = longitude.toString()

        return update
    }
}
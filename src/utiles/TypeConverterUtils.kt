package com.george.utiles

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TypeConverterUtils {

    val gson = Gson()

    //convert a data class to a map
    fun <T> T.serializeToMap(): Map<String, Any> {
        return convert()
    }

    //convert a map to a data class
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    //convert an object of type I to type O
    inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    /*//example usage
    data class Person(val name: String, val age: Int)

    fun main() {

        val person = Person("Tom Hanley", 99)

        val map = mapOf(
            "name" to "Tom Hanley",
            "age" to 99
        )

        val personAsMap: Map<String, Any> = person.serializeToMap()

        val mapAsPerson: Person = map.toDataClass()
    }*/

}
package com.example.digitaldiaryba.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// START https://stackoverflow.com/questions/33381384/how-to-use-typetoken-generics-with-gson-in-kotlin
class ListTypeConverter {
    inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            Gson().fromJson<List<String>>(value)
        } catch (e: Exception) {
            listOf<String>()
        }
    }
}
// END https://stackoverflow.com/questions/33381384/how-to-use-typetoken-generics-with-gson-in-kotlin
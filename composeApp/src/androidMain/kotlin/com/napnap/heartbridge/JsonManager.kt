package com.napnap.heartbridge

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

object JsonManager {

    const val FILE_NAME = "data.json"
    private fun loadFromJson(context: Context): String{
        val file = File(context.filesDir, FILE_NAME)

        if (!file.exists()) {
            throw FileNotFoundException("Locale file $FILE_NAME not found in internal storage")
        }

        return file.bufferedReader().use { it.readText() }
    }

    private fun saveToJson(context: Context,content: String) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(content)
    }

    fun load(context: Context): List<Measurement>{
        val jsonString = loadFromJson(context)
        return Json.decodeFromString<List<Measurement>>(jsonString)
    }

    fun save(context: Context,data: List<Measurement>) {
        val jsonString = Json.encodeToString(data)
        saveToJson(context,jsonString)
    }
}
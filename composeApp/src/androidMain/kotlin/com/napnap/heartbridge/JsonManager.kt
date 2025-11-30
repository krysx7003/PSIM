package com.napnap.heartbridge

import android.content.Context
import android.util.Log
import com.napnap.heartbridge.ui.components.SettingsStore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object JsonManager {
    private var data: List<Measurement> = emptyList()
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
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
        data = Json.decodeFromString<List<Measurement>>(jsonString)
        return data.sortedByDescending { it.id }
    }

    private fun shouldDelete(context: Context): Boolean{
        if (data.size < 2) return false

        val highId = data.maxByOrNull { it.id }
        val lowId = data.minByOrNull { it.id }

        if (highId == null || lowId == null) return false

        try {
            val localDate1 = LocalDate.parse(highId.date, dateFormatter)
            val localDate2 = LocalDate.parse(lowId.date, dateFormatter)
            val days = ChronoUnit.DAYS.between(localDate1, localDate2)
            if(days > SettingsStore.readS(context,"history","30").toInt())
                return true
        } catch (e: Exception) {
            Log.e("JSON_DELETE","Couldn't parse date reason: $e")
        }
        return false
    }

    fun append(context: Context,value: Measurement):List<Measurement> {
        data = data + value
        if(shouldDelete(context)){
            val lowestId = data.minByOrNull { it.id }?.id
            data = data.filter { it.id != lowestId }
        }

        val jsonString = Json.encodeToString(data)
        saveToJson(context,jsonString)
        return data.sortedByDescending { it.id }
    }

    fun save(context: Context,list: List<Measurement>) {
        val jsonString = Json.encodeToString(list)
        saveToJson(context,jsonString)
    }
}
package com.napnap.heartbridge

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.napnap.heartbridge.ui.components.SettingsStore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.text.toInt

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

    fun appendMany(context: Context,measurements: List<Int>,name: String){
        if (measurements.isEmpty()) return

        val averageBpm = measurements.average().toInt()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val nextId = try {
            val currentData = load(context)
            (currentData.maxByOrNull { it.id }?.id ?: 0) + 1
        } catch (e: Exception) {
            Log.e("JSON_DELETE","Couldn't parse date reason: $e")
            1
        }
        val averageMeasurement = Measurement(
            id = nextId,
            date = currentDate,
            hour = currentTime,
            bpm = averageBpm.toString(),
            device = name
        )
        append(context, averageMeasurement)
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
    fun saveCSV(context: Context){
        val csvHeader = "ID,Date,Hour,BPM,Device\n"

        val csvContent = StringBuilder().apply {
            append(csvHeader)
            data.forEach { item ->
                append("${item.id},${item.date},${item.hour},${item.bpm},${item.device}\n")
            }
        }.toString()

        saveToDownloads(context,csvContent)
    }
    fun saveToDownloads(context: Context, csvContent: String) {
        try {
            val timeStamp = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
            val fileName = "heart_rate_data_$timeStamp.csv"

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(csvContent.toByteArray())
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                FileOutputStream(file).use { outputStream ->
                    outputStream.write(csvContent.toByteArray())
                }
            }
            Toast.makeText(context, "CSV saved to Downloads", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun save(context: Context,list: List<Measurement>) {
        val jsonString = Json.encodeToString(list)
        saveToJson(context,jsonString)
    }
}
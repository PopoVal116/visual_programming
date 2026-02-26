package com.example.visual_programming2.supp

import android.content.Context
import android.os.Environment
import com.example.visual_programming2.data.DeviceData  // новый класс
import com.google.gson.Gson
import java.io.File
object DataSaver {
    private val gson = Gson()

    fun save(context: Context, data: DeviceData) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "device_data.json")

        try {
            val json = gson.toJson(data)

            if (!file.exists()) {
                file.writeText("[\n  $json\n]")
            } else {
                val content = file.readText()
                val newContent = content.dropLast(1) + ",\n  $json\n]"
                file.writeText(newContent)
            }
        } catch (e: Exception) {

        }
    }
}
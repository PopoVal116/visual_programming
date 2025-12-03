package com.example.visual_programming2.supp

import android.content.Context
import android.os.Environment
import com.example.visual_programming2.data.LocationRecord
import com.google.gson.Gson
import java.io.File

object LocationSaver {
    private val gson = Gson()

    fun save(context: Context, record: LocationRecord) {
        val json = gson.toJson(record)
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "my_locations.json")

        try {
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
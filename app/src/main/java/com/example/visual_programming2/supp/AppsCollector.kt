package com.example.visual_programming2.supp

import android.content.pm.PackageManager

object AppsCollector {
    fun collectTopApps(packageManager: PackageManager): String {
        val result = StringBuilder()
        result.append("TOP APPS\n")

        try {
            val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val topApps = apps.take(5)
            for ((index, app) in topApps.withIndex()) {
                val appName = packageManager.getApplicationLabel(app)
                result.append("${index+1}. ${appName}\n")
            }
        } catch (e: Exception) {
            result.append("APPS_ERROR: ${e.message}\n")
        }
        return result.toString()
    }
}
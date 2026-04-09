package com.example.visual_programming2.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import org.zeromq.ZMQ
import com.example.visual_programming2.supp.DataBuilder
import com.example.visual_programming2.supp.PermissionUtils
import com.google.android.gms.location.*
import android.os.Looper
import java.util.concurrent.TimeUnit
import android.location.Location
import android.telephony.TelephonyManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import java.io.File

class BackgroundService : Service() {

    val LOG_TAG: String = "BG_SERVICE"
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var telephonyManager: TelephonyManager
    private var isSending = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "bg_channel",
                "Фоновый сервис",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        Log.d(LOG_TAG, "Сервис создан")
    }

    private fun saveToJson(data: String) {
        try {
            val file = File(filesDir, "data.json")
            file.appendText("{\"data\":\"$data\"}\n")
            Log.d(LOG_TAG, "Данные записаны в файл: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Ошибка записи в файл: ${e.message}")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun sendMessageToActivity(msg: String?) {
        val intent = Intent("BackGroundUpdate")
        intent.putExtra("Status", msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        Log.d(LOG_TAG, "Сообщение отправлено в Activity: $msg")
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "bg_channel")
            .setContentTitle("Сбор данных")
            .setContentText("Сервис работает")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1001, notification)
        Log.d(LOG_TAG, "Сервис запущен")
        if (!PermissionUtils.checkLocationPermission(this) ||
            !PermissionUtils.checkPhoneStatePermission(this)) {
            Log.w(LOG_TAG, "onStartCommand: Нет разрешений — останавливаем")
            sendMessageToActivity("Нет разрешений сервис остановлен")
            stopSelf()
            return START_NOT_STICKY
        }
        Log.d(LOG_TAG, "Разрешения прошли, запускаем цикл")

        isSending = true

        serviceScope.launch {
            Log.d(LOG_TAG, "Корутина запущена, входим в while")
            var count = 0
            while (isActive && isSending){
                count++
                delay(1000)
                Log.d(LOG_TAG, "Начинаем итерацию $count")
                val location = getCurrentLocation()
                val statusMsg = if (location != null) {
                    val record = DataBuilder.buildAllData(location, telephonyManager, packageManager)
                    val dataToSend = DataBuilder.recordToString(record)
                    saveToJson(dataToSend)
                    sendZMQ(dataToSend)
                    "Пакет $count отправлен и сохранен"
                } else {
                    "Пакет $count: локация не получена"
                }
                sendMessageToActivity(statusMsg)
            }
            Log.d(LOG_TAG, "Цикл завершён, вызываем stopSelf()")
            stopSelf()
        }
        Log.d(LOG_TAG, "onStartCommand: возвращаем START_STICKY")
        return START_STICKY
    }

    private suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { cont ->

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000
            ).setMinUpdateIntervalMillis(1000).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    fusedLocationClient.removeLocationUpdates(this)

                    if (!cont.isCompleted) {
                        cont.resume(location, null)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            cont.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }

    private fun sendZMQ(data: String) {
        try {
            val context = ZMQ.context(1)
            val socket = context.socket(ZMQ.REQ)
            socket.connect("tcp://172.20.10.12:5555")

            socket.send(data.toByteArray(ZMQ.CHARSET), 0)
            Log.d(LOG_TAG, "Отправлен пакет: $data")
            val reply = socket.recv(0)
            socket.close()
            context.close()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "ZMQ ошибка: ${e.message}")
        }
    }


    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: Сервис уничтожен")
        isSending = false
        super.onDestroy()
        serviceJob.cancel()
        Log.d(LOG_TAG, "Service destroyed")
    }
}
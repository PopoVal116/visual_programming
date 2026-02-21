package com.example.visual_programming2.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visual_programming2.R
import org.zeromq.ZMQ
import com.example.visual_programming2.data.LocationRecord
import com.example.visual_programming2.supp.PermissionUtils
import com.example.visual_programming2.supp.LocationSaver
import com.google.gson.Gson
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.*
import android.annotation.SuppressLint



class SocketActivity : AppCompatActivity() {
    private val log_tag = "MY_LOG_TAG"
    private lateinit var tvSockets: TextView
    private lateinit var btnSendToPc: Button
    private lateinit var handler: Handler
    private var isSending = false
    private var counter = 0
    private lateinit var clientThread: Thread
    private lateinit var locationManager: LocationManager
    private val gson = Gson()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socket)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvSockets = findViewById(R.id.tvSockets)
        btnSendToPc = findViewById(R.id.btn_send_to_pc)
        handler = Handler(Looper.getMainLooper())
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        btnSendToPc.setOnClickListener {
            if (!isSending) {
                Thread {
                    startClient()
                }.start()
            } else {
                stopClient()
            }
        }
    }

    //fun startServer() {
    //  val context = ZMQ.context(1)
    //val socket = context.socket(ZMQ.REP)
    //socket.bind("tcp://*:5556")
    //var counter = 0

    //while (true) {
    //counter++
    //val requestBytes = socket.recv(0)
    //val request = String(requestBytes, ZMQ.CHARSET)
    //Log.d(log_tag, "[SERVER] Received request: [$request]")

    //handler.post {
    //tvSockets.text = "Сервер: получено $counter сообщений"
    //}
    //Thread.sleep(1000)

    //val response = "Hello from Android ZMQ Server!"
    //socket.send(response.toByteArray(ZMQ.CHARSET), 0)
    //}
    //}

    fun startClient() {
        if (isSending) {
            return
        }

        if (!PermissionUtils.checkLocationPermission(this)) {
            handler.post {
                tvSockets.text = "Нет разрешения на геолокацию"
            }
            return
        }

        isSending = true
        counter = 0
        handler.post {
            btnSendToPc.text = "ОСТАНОВИТЬ"
            tvSockets.text = "Получаю локацию..."
        }

        clientThread = Thread {
            val context = ZMQ.context(1)
            val socket = context.socket(ZMQ.REQ)
            socket.connect("tcp://172.20.10.12:5555")

            while (isSending) {
                try {
                    counter++
                    val latch = java.util.concurrent.CountDownLatch(1)
                    var currentLocation: Location? = null

                    requestSingleLocation { location ->
                        currentLocation = location
                        latch.countDown()
                    }


                    latch.await(5, java.util.concurrent.TimeUnit.SECONDS)

                    if (currentLocation == null) {
                        handler.post {
                            tvSockets.text = "Не могу получить локацию"
                        }
                        Thread.sleep(2000)
                        continue
                    }

                    val request = "${currentLocation!!.latitude},${currentLocation!!.longitude},${currentLocation!!.altitude},${currentLocation!!.time}"
                    socket.send(request.toByteArray(ZMQ.CHARSET), 0)
                    Log.d(log_tag, "[CLIENT] Отправил: $request")

                    val reply = socket.recv(0)
                    val replyText = String(reply, ZMQ.CHARSET)

                    val locationForUI = currentLocation
                    val counterForUI = counter
                    val replyForUI = replyText

                    handler.post {
                        tvSockets.text = """
                            Отправлено: $counterForUI
                            Широта: ${locationForUI!!.latitude}
                            Долгота: ${locationForUI!!.longitude}
                            Ответ: $replyForUI
                        """.trimIndent()
                    }

                    Thread.sleep(2000)
                } catch (e: Exception) {
                    Log.e(log_tag, "[CLIENT] Ошибка: ${e.message}")
                    handler.post {
                        tvSockets.text = "Ошибка: ${e.message}"
                    }
                    break
                }
            }

            socket.close()
            context.close()
        }

        clientThread.start()
    }

    fun stopClient() {
        isSending = false
        //btnSendToPc.text = "ОТПРАВИТЬ НА ПК"

        handler.post {
            btnSendToPc.text = "ОТПРАВИТЬ НА ПК"
            tvSockets.text = "Остановлено. Отправлено: $counter"
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestSingleLocation(onLocationReceived: (Location?) -> Unit) {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).setMinUpdateIntervalMillis(1000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                onLocationReceived(locationResult.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        isSending = false
        if (::clientThread.isInitialized && clientThread.isAlive) {
            clientThread.join(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        //Thread { startServer() }.start()
        //Thread.sleep(1000)
        //Thread { startClient() }.start()
    }
}
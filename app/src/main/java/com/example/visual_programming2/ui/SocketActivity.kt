package com.example.visual_programming2.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visual_programming2.R
import org.zeromq.ZMQ

class SocketActivity : AppCompatActivity() {
    private val log_tag = "MY_LOG_TAG"
    private lateinit var tvSockets: TextView
    private lateinit var handler: Handler

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
        handler = Handler(Looper.getMainLooper())
    }

    //fun startServer() {
      //  val context = ZMQ.context(1)
        //val socket = context.socket(ZMQ.REP)
        //socket.bind("tcp://*:2222")
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
        val context = ZMQ.context(1)
        val socket = context.socket(ZMQ.REQ)
        socket.connect("tcp://192.168.0.164:5556")
        val request = "Hello from Android!"
        var counter = 0

        while (true) {
            counter++
            socket.send(request.toByteArray(ZMQ.CHARSET), 0)
            Log.d(log_tag, "[CLIENT] Отправил: $request ($counter)")

            val reply = socket.recv(0)
            val replyText = String(reply, ZMQ.CHARSET)
            Log.d(log_tag, "[CLIENT] Получил: $replyText")

            handler.post {
                tvSockets.text = "Клиент: отправлено $counter\nОтвет: $replyText"
            }
            Thread.sleep(2000)
        }
    }

    override fun onResume() {
        super.onResume()
        //Thread { startServer() }.start()
        Thread.sleep(1000)
        Thread { startClient() }.start()
    }
}
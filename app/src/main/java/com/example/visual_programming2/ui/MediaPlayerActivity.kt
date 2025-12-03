package com.example.visual_programming2.ui

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.visual_programming2.R
import com.example.visual_programming2.supp.PermissionUtils
import java.io.File

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewTime: TextView
    private lateinit var seekBarPosition: SeekBar
    private lateinit var seekBarVolume: SeekBar
    private lateinit var seekBarValue: TextView
    private lateinit var musicListView: ListView
    private lateinit var buttonPlayPause: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonForward: Button
    private lateinit var buttonRewind: Button

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler

    private var musicFiles: List<File> = emptyList()
    private var currentSong = 0
    private var isStopped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        textViewTitle = findViewById(R.id.textViewTitle)
        textViewTime = findViewById(R.id.textViewTime)
        seekBarPosition = findViewById(R.id.seekBarPosition)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        seekBarValue = findViewById(R.id.seekBarValue)
        musicListView = findViewById(R.id.musicListView)
        buttonPlayPause = findViewById(R.id.buttonPlayPause)
        buttonStop = findViewById(R.id.buttonStop)
        buttonForward = findViewById(R.id.buttonForward)
        buttonRewind = findViewById(R.id.buttonRewind)

        mediaPlayer = MediaPlayer()
        handler = Handler()

        seekBarVolume.progress = 70
        seekBarValue.text = "70"
        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val volume = progress / 100f
                    mediaPlayer.setVolume(volume, volume)
                    seekBarValue.text = progress.toString()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarPosition.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewTime.text = formatTime(progress)
                if (fromUser && mediaPlayer.isPlaying) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                buttonPlayPause.text = "▶"
            } else {
                if (isStopped) {
                    playMusic()
                } else if (mediaPlayer.currentPosition > 0) {
                    mediaPlayer.start()
                    buttonPlayPause.text = "||"
                    startUpdatingProgress()
                } else {
                    if (musicFiles.isNotEmpty()) {
                        playMusic()
                    } else {
                        if (!PermissionUtils.checkMediaPermission(this)) {
                            PermissionUtils.requestMediaPermission(this)
                        } else {
                            loadMusic()
                        }
                    }
                }
            }
        }

        buttonStop.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            seekBarPosition.progress = 0
            textViewTime.text = "0:00"
            buttonPlayPause.text = "▶"
            isStopped = true
        }

        buttonForward.setOnClickListener {
            if (musicFiles.isNotEmpty()) {
                currentSong++
                if (currentSong >= musicFiles.size) {
                    currentSong = 0
                }
                playMusic()
            }
        }

        buttonRewind.setOnClickListener {
            if (musicFiles.isNotEmpty()) {
                currentSong--
                if (currentSong < 0) {
                    currentSong = musicFiles.size - 1
                }
                playMusic()
            }
        }

        musicListView.setOnItemClickListener { _, _, position, _ ->
            currentSong = position
            playMusic()
            buttonPlayPause.text = "||"
        }

        if (!PermissionUtils.checkMediaPermission(this)) {
            PermissionUtils.requestMediaPermission(this)
        } else {
            loadMusic()
        }
    }

    private fun loadMusic() {
        val musicPath = Environment.getExternalStorageDirectory().path + "/Music"
        val folder = File(musicPath)

        musicFiles = folder.listFiles()?.filter { file ->
            file.extension == "mp3" || file.extension == "wav" || file.extension == "aac"
        } ?: emptyList()

        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "Музыка не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val songNames = mutableListOf<String>()
        for (file in musicFiles) {
            songNames.add(file.name)
        }

        musicListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songNames)
    }

    private fun playMusic() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            if (musicFiles.isEmpty()) return

            val musicFile = musicFiles[currentSong]
            textViewTitle.text = musicFile.name
            mediaPlayer.setDataSource(musicFile.absolutePath)
            mediaPlayer.prepare()
            seekBarPosition.max = mediaPlayer.duration
            mediaPlayer.start()
            buttonPlayPause.text = "||"
            isStopped = false

            startUpdatingProgress()

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startUpdatingProgress() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val pos = mediaPlayer.currentPosition
                    seekBarPosition.progress = pos
                    textViewTime.text = formatTime(pos)
                    handler.postDelayed(this, 100)
                }
            }
        })
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusic()
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
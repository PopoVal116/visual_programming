package com.example.visual_programming2

import android.Manifest
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

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

    private val songTitles = ArrayList<String>()
    private val songUris = ArrayList<Uri>()
    private var currentSong = 0

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            loadMusicFromDevice()
        } else {
            Toast.makeText(this, "Нет доступа к музыке", Toast.LENGTH_LONG).show()
        }
    }

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
                if (fromUser) {
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
                if (mediaPlayer.currentPosition > 0) {
                    mediaPlayer.start()
                    buttonPlayPause.text = "||"
                    startUpdatingProgress()
                } else if (songTitles.isNotEmpty()) {
                    playMusic()
                }
            }
        }

        buttonStop.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            seekBarPosition.progress = 0
            textViewTime.text = "0:00"
            buttonPlayPause.text = "▶"
            handler.removeCallbacksAndMessages(null)
        }

        buttonForward.setOnClickListener {
            if (songTitles.isNotEmpty()) {
                currentSong = (currentSong + 1) % songTitles.size
                playMusic()
            }
        }

        buttonRewind.setOnClickListener {
            if (songTitles.isNotEmpty()) {
                currentSong = currentSong - 1
                if (currentSong < 0) {
                    currentSong = songTitles.size - 1
                }
                playMusic()
            }
        }

        requestPermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
    }

    private fun loadMusicFromDevice() {
        songTitles.clear()
        songUris.clear()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE
        )

        contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
            val idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val uri = Uri.withAppendedPath(collection, id.toString())

                songTitles.add(title)
                songUris.add(uri)
            }
        }

        if (songTitles.isEmpty()) {
            Toast.makeText(this, "Музыка не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        musicListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songTitles)
        musicListView.setOnItemClickListener { _, _, position, _ ->
            currentSong = position
            playMusic()
            buttonPlayPause.text = "||"
        }
    }

    private fun playMusic() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            mediaPlayer.setDataSource(this, songUris[currentSong])
            mediaPlayer.prepare()
            mediaPlayer.start()

            textViewTitle.text = songTitles[currentSong]
            seekBarPosition.max = mediaPlayer.duration
            seekBarPosition.progress = 0
            textViewTime.text = "0:00"

            startUpdatingProgress()

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startUpdatingProgress() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val pos = mediaPlayer.currentPosition
                    seekBarPosition.progress = pos
                    textViewTime.text = formatTime(pos)
                    handler.postDelayed(this, 1000)
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

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }
}
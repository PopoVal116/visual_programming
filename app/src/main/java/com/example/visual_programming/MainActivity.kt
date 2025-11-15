package com.example.visual_programming2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonCalculator = findViewById<Button>(R.id.buttonCalculator)
        buttonCalculator.setOnClickListener {
            val calculatorIntent = Intent(this, CalculatorActivity::class.java)
            startActivity(calculatorIntent)
        }

        val buttonMediaPlayer = findViewById<Button>(R.id.buttonMediaPlayer)
        buttonMediaPlayer.setOnClickListener {
            val mediaIntent = Intent(this, MediaPlayerActivity::class.java)
            startActivity(mediaIntent)
        }

        val buttonLocation = findViewById<Button>(R.id.buttonLocation)
        buttonLocation.setOnClickListener {
            val locationIntent = Intent(this, LocationActivity::class.java)
            startActivity(locationIntent)
        }
    }
}
package com.example.visual_programming2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var button0: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonMultiplication: Button
    private lateinit var buttonDivision: Button
    private lateinit var buttonEquals: Button
    private lateinit var buttonC: Button

    private var firstNumber: Double = 0.0
    private var secondNumber: Double = 0.0
    private var currentOperator: String = ""
    private var isNewOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView) ?: run {
            textView = TextView(this).apply { text = "TextView not found" }
            setContentView(textView)
            return
        }
        button0 = findViewById(R.id.button0) ?: return
        button1 = findViewById(R.id.button1) ?: return
        button2 = findViewById(R.id.button2) ?: return
        button3 = findViewById(R.id.button3) ?: return
        button4 = findViewById(R.id.button4) ?: return
        button5 = findViewById(R.id.button5) ?: return
        button6 = findViewById(R.id.button6) ?: return
        button7 = findViewById(R.id.button7) ?: return
        button8 = findViewById(R.id.button8) ?: return
        button9 = findViewById(R.id.button9) ?: return
        buttonPlus = findViewById(R.id.buttonPlus) ?: return
        buttonMinus = findViewById(R.id.buttonMinus) ?: return
        buttonMultiplication = findViewById(R.id.buttonMultiplication) ?: return
        buttonDivision = findViewById(R.id.buttonDivision) ?: return
        buttonEquals = findViewById(R.id.buttonEquals) ?: return
        buttonC = findViewById(R.id.buttonC) ?: run {
            buttonC = Button(this).apply { text = "C" }
            setContentView(buttonC)
            return
        }

        button0.setOnClickListener { appendToText("0") }
        button1.setOnClickListener { appendToText("1") }
        button2.setOnClickListener { appendToText("2") }
        button3.setOnClickListener { appendToText("3") }
        button4.setOnClickListener { appendToText("4") }
        button5.setOnClickListener { appendToText("5") }
        button6.setOnClickListener { appendToText("6") }
        button7.setOnClickListener { appendToText("7") }
        button8.setOnClickListener { appendToText("8") }
        button9.setOnClickListener { appendToText("9") }

        buttonPlus.setOnClickListener { setOperator("+") }
        buttonMinus.setOnClickListener { setOperator("-") }
        buttonMultiplication.setOnClickListener { setOperator("*") }
        buttonDivision.setOnClickListener { setOperator("/") }

        buttonEquals.setOnClickListener { calculateResult() }

        buttonC.setOnClickListener {
            firstNumber = 0.0
            secondNumber = 0.0
            currentOperator = ""
            isNewOperation = true
            textView.text = "0"
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun appendToText(digit: String) {
        if (isNewOperation || textView.text.toString() == "0") {
            textView.text = digit
            isNewOperation = false
        } else {
            textView.text = textView.text.toString() + digit
        }
    }

    private fun setOperator(operator: String) {
        if (textView.text.toString().isNotEmpty()) {
            firstNumber = textView.text.toString().toDoubleOrNull() ?: 0.0
        }
        currentOperator = operator
        isNewOperation = true
        textView.text = ""
    }

    private fun calculateResult() {
        if (currentOperator.isNotEmpty() && textView.text.toString().isNotEmpty()) {
            secondNumber = textView.text.toString().toDoubleOrNull() ?: 0.0
            when (currentOperator) {
                "+" -> textView.text = (firstNumber + secondNumber).toString()
                "-" -> textView.text = (firstNumber - secondNumber).toString()
                "*" -> textView.text = (firstNumber * secondNumber).toString()
                "/" -> {
                    if (secondNumber != 0.0) {
                        textView.text = (firstNumber / secondNumber).toString()
                    } else {
                        textView.text = "Error"
                    }
                }
                else -> textView.text = "Error"
            }
            firstNumber = textView.text.toString().toDoubleOrNull() ?: 0.0
            isNewOperation = true
        } else if (currentOperator.isEmpty()) {
            firstNumber = textView.text.toString().toDoubleOrNull() ?: 0.0
        }
        secondNumber = 0.0
        currentOperator = ""
    }
}
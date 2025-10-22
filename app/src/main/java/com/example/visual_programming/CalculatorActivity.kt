package com.example.visual_programming2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.visual_programming2.R

class CalculatorActivity : AppCompatActivity() {
    private var currentNumber = ""
    private var firstNumber = 0.0
    private var currentOperator = ""
    private var nextOperator = false
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculator)

        resultText = findViewById(R.id.resultText)

        val button0 = findViewById<Button>(R.id.button0)
        button0.setOnClickListener {
            appendDigit("0")
        }

        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            appendDigit("1")
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            appendDigit("2")
        }

        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            appendDigit("3")
        }

        val button4 = findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            appendDigit("4")
        }

        val button5 = findViewById<Button>(R.id.button5)
        button5.setOnClickListener {
            appendDigit("5")
        }

        val button6 = findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            appendDigit("6")
        }

        val button7 = findViewById<Button>(R.id.button7)
        button7.setOnClickListener {
            appendDigit("7")
        }

        val button8 = findViewById<Button>(R.id.button8)
        button8.setOnClickListener {
            appendDigit("8")
        }

        val button9 = findViewById<Button>(R.id.button9)
        button9.setOnClickListener {
            appendDigit("9")
        }

        val buttonPlus = findViewById<Button>(R.id.buttonPlus)
        buttonPlus.setOnClickListener {
            handleOperator("+")
        }

        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
        buttonMinus.setOnClickListener {
            handleOperator("-")
        }

        val buttonMultiplication = findViewById<Button>(R.id.buttonMultiplication)
        buttonMultiplication.setOnClickListener {
            handleOperator("×")
        }

        val buttonDivision = findViewById<Button>(R.id.buttonDivision)
        buttonDivision.setOnClickListener {
            handleOperator("÷")
        }

        val buttonC = findViewById<Button>(R.id.buttonC)
        buttonC.setOnClickListener {
            resetCalculator()
        }

        val buttonEquals = findViewById<Button>(R.id.buttonEquals)
        buttonEquals.setOnClickListener {
            computeResult()
        }
    }

    private fun appendDigit(digit: String) {
        if (nextOperator) {
            currentNumber = digit
            nextOperator = false
        } else {
            currentNumber += digit
        }
        resultText.text = currentNumber
    }

    private fun handleOperator(operator: String) {
        if (currentNumber.isEmpty()) return

        if (!nextOperator) {
            firstNumber = currentNumber.toDouble()
        }

        currentOperator = operator
        currentNumber = ""
        nextOperator = true
    }

    private fun computeResult() {
        if (currentOperator.isEmpty() || currentNumber.isEmpty()) return

        val secondNumber = currentNumber.toDouble()
        performComputation(secondNumber)

        currentOperator = ""
        nextOperator = true
    }

    private fun performComputation(secondNumber: Double): Boolean {
        var result = 0.0
        when (currentOperator) {
            "+" -> result = firstNumber + secondNumber
            "-" -> result = firstNumber - secondNumber
            "×" -> result = firstNumber * secondNumber
            "÷" -> {
                if (secondNumber == 0.0) {
                    resultText.text = "Error"
                    resetCalculator()
                    return false
                }
                result = firstNumber / secondNumber
            }
        }
        val resultString = result.toString().removeSuffix(".0")
        resultText.text = resultString
        firstNumber = result
        currentNumber = resultString
        return true
    }

    private fun resetCalculator() {
        currentNumber = ""
        firstNumber = 0.0
        currentOperator = ""
        nextOperator = false
        resultText.text = "0"
    }
}
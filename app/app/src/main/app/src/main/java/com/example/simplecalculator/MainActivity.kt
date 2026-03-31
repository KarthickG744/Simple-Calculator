package com.example.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvDisplay: TextView

    private var currentInput = StringBuilder()
    private var expression = StringBuilder()
    private var operator = ""
    private var firstOperand = 0.0
    private var isOperatorPressed = false
    private var isResultShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)

        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnDot,
            R.id.btnAdd, R.id.btnSubtract,
            R.id.btnMultiply, R.id.btnDivide,
            R.id.btnEquals, R.id.btnClear,
            R.id.btnPlusMinus, R.id.btnPercent
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onButtonClick(it) }
        }
    }

    private fun onButtonClick(view: View) {
        val btn = view as Button
        when (val btnText = btn.text.toString()) {
            "C"         -> clear()
            "+/-"       -> toggleSign()
            "%"         -> percentage()
            "="         -> calculate()
            "+", "-", "×", "÷" -> setOperator(btnText)
            "."         -> addDecimal()
            else        -> appendNumber(btnText)
        }
    }

    private fun appendNumber(num: String) {
        if (isResultShown) {
            currentInput.clear()
            expression.clear()
            isResultShown = false
        }
        if (isOperatorPressed) {
            currentInput.clear()
            isOperatorPressed = false
        }
        if (currentInput.length < 12) {
            currentInput.append(num)
            tvDisplay.text = currentInput.toString()
        }
    }

    private fun addDecimal() {
        if (isResultShown) {
            currentInput.clear()
            isResultShown = false
        }
        if (isOperatorPressed) {
            currentInput.clear()
            isOperatorPressed = false
        }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput.append("0")
            currentInput.append(".")
            tvDisplay.text = currentInput.toString()
        }
    }

    private fun setOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            firstOperand = currentInput.toString().toDouble()
            expression.clear()
            expression.append(formatNumber(firstOperand)).append(" $op")
            tvExpression.text = expression.toString()
        } else if (operator.isNotEmpty()) {
            // Replace operator
            val expStr = expression.toString().dropLast(1) + op
            expression.clear()
            expression.append(expStr)
            tvExpression.text = expression.toString()
        }
        operator = op
        isOperatorPressed = true
        isResultShown = false
    }

    private fun calculate() {
        if (operator.isEmpty() || currentInput.isEmpty()) return

        val secondOperand = currentInput.toString().toDouble()
        expression.append(" ${formatNumber(secondOperand)} =")
        tvExpression.text = expression.toString()

        val result = when (operator) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "×" -> firstOperand * secondOperand
            "÷" -> {
                if (secondOperand == 0.0) {
                    tvDisplay.text = "Error"
                    tvExpression.text = "Cannot divide by zero"
                    clear()
                    return
                }
                firstOperand / secondOperand
            }
            else -> 0.0
        }

        val formatted = formatNumber(result)
        tvDisplay.text = formatted
        currentInput = StringBuilder(formatted)
        operator = ""
        isOperatorPressed = false
        isResultShown = true
    }

    private fun clear() {
        currentInput.clear()
        expression.clear()
        operator = ""
        firstOperand = 0.0
        isOperatorPressed = false
        isResultShown = false
        tvDisplay.text = "0"
        tvExpression.text = ""
    }

    private fun toggleSign() {
        if (currentInput.isNotEmpty() && currentInput.toString() != "0") {
            val value = currentInput.toString().toDouble() * -1
            val formatted = formatNumber(value)
            currentInput = StringBuilder(formatted)
            tvDisplay.text = formatted
        }
    }

    private fun percentage() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toString().toDouble() / 100
            val formatted = formatNumber(value)
            currentInput = StringBuilder(formatted)
            tvDisplay.text = formatted
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble())
            value.toLong().toString()
        else
            value.toBigDecimal().stripTrailingZeros().toPlainString()
    }
}

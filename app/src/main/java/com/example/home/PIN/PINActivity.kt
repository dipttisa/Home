package com.example.home.PIN

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.home.MainActivity
import com.example.home.R

class PINActivity : AppCompatActivity() {
    private lateinit var buttonBackspace: ImageButton
    private lateinit var pinDot1: ImageView
    private lateinit var pinDot2: ImageView
    private lateinit var pinDot3: ImageView
    private lateinit var pinDot4: ImageView
    private var currentPinIndex = 0
    private val pinDigits = mutableListOf<String>() // Сохраняет цифры PIN


    private fun getEnteredPin(): String {
        // Аналогично: соберите введённый PIN-код в строку
        return pinDigits.joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)


        pinDot1 = findViewById(R.id.pinDot1)
        pinDot2 = findViewById(R.id.pinDot2)
        pinDot3 = findViewById(R.id.pinDot3)
        pinDot4 = findViewById(R.id.pinDot4)



        buttonBackspace = findViewById(R.id.buttonBackspace)


        val buttons = listOf(
            findViewById<Button>(R.id.button0),
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9)
        )


        buttons.forEach { button ->
            button.setOnClickListener {
                if (currentPinIndex < 4) {
                    val digit = button.text.toString() // Получите текст кнопки
                    pinDigits.add(digit)              // Добавьте в список
                    updatePinCircle(currentPinIndex)  // Обновите индикатор
                    currentPinIndex++

                    if (currentPinIndex == 4) {
                        onPinComplete()
                    }
                }
            }
        }


        buttonBackspace.setOnClickListener {
            if (currentPinIndex > 0) {
                currentPinIndex--
                clearLastPinCircle()
            }
        }
    }

    private fun updatePinCircle(index: Int) {
        when (index) {
            0 -> pinDot1.setImageResource(R.drawable.pinzakr)
            1 -> pinDot2.setImageResource(R.drawable.pinzakr)
            2 -> pinDot3.setImageResource(R.drawable.pinzakr)
            3 -> pinDot4.setImageResource(R.drawable.pinzakr)
        }
    }



    private fun clearLastPinCircle() {
        when (currentPinIndex) {
            0 -> pinDot1.setImageResource(R.drawable.pinnezakr)
            1 -> pinDot2.setImageResource(R.drawable.pinnezakr)
            2 -> pinDot3.setImageResource(R.drawable.pinnezakr)
            3 -> pinDot4.setImageResource(R.drawable.pinnezakr)
        }
    }

    private fun resetPin() {
        pinDot1.setImageResource(R.drawable.pinnezakr)
        pinDot2.setImageResource(R.drawable.pinnezakr)
        pinDot3.setImageResource(R.drawable.pinnezakr)
        pinDot4.setImageResource(R.drawable.pinnezakr)
        currentPinIndex = 0
    }



    private fun onPinComplete() {
        val enteredPin = getEnteredPin() // Получите PIN-код
        val sharedPreferences = getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("savedPin", null)

        if (enteredPin == savedPin) {
            Toast.makeText(this, "PIN-код верный!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Неверный PIN-код!", Toast.LENGTH_SHORT).show()
            resetPin() // Сбросить индикатор ввода
        }



    }
}
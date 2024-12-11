package com.example.home.PIN

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.home.R

class SozdanPINActivity : AppCompatActivity() {

    private lateinit var buttonBackspace: ImageButton
    private lateinit var pinDot1: ImageView
    private lateinit var pinDot2: ImageView
    private lateinit var pinDot3: ImageView
    private lateinit var pinDot4: ImageView
    private var currentPinIndex = 0

    private val pinDigits = mutableListOf<String>() // Сохраняет цифры PIN

    private fun getEnteredPin(): String {
        // Реализуйте логику для получения полного PIN-кода, введённого пользователем.
        // Например, сохраняйте нажатия кнопок в список `pinDigits`.
        return pinDigits.joinToString("")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sozdan_pinactivity)

        // Инициализация ImageView для кружков
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

    private fun onPinComplete() {
        val enteredPin = getEnteredPin() // Получите введённый PIN-код
        savePinCode(enteredPin)         // Сохраните его
        Toast.makeText(this, "PIN-код установлен!", Toast.LENGTH_SHORT).show()
        finish()
        val intent = Intent(this, PINActivity::class.java)
        startActivity(intent)

    }

    private fun savePinCode(pinCode: String) {
        val sharedPreferences = getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("savedPin", pinCode).apply()
        sharedPreferences.edit().putBoolean("hasPinCode", true).apply()
    }
}

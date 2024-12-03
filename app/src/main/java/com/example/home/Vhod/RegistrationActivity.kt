package com.example.home.Vhod

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.home.MainActivity
import com.example.home.R

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var Button: Button
    private lateinit var Button2: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userEditText = findViewById(R.id.editTextTextUserName)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextNumberPassword)
        Button = findViewById(R.id.button)
        Button2 = findViewById(R.id.button2)
//tttttttttt
//////dqwdwqdqwdqwdqdqw
        Button.setOnClickListener {
            validateFields()
        }
        Button2.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

        }
    }

    private fun validateFields() {
        val user = userEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(user)) {
            userEditText.error = "Это поле обязательно"
            isValid = false
        } else {
            userEditText.error = null

        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Это поле обязательно"
            isValid = false
        } else {
            emailEditText.error = null
            if (TextUtils.isEmpty(email) || !email.contains("@")) {
                emailEditText.error = "Неверный формат email"
                isValid = false
            } else {
                emailEditText.error = null
            }
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Это поле обязательно"
            isValid = false
        } else {
            passwordEditText.error = null
        }

        if (isValid) {
            // Здесь можно добавить логику авторизации
            Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
        }
        if (isValid) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля правильно", Toast.LENGTH_SHORT).show()
        }
    }
}

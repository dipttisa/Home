package com.example.home.Vhod

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.home.Address.AdrActivity
import com.example.home.PIN.PINActivity
import com.example.home.PIN.SozdanPINActivity
import com.example.home.R
import com.example.home.sb.SupB
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class RegistrationActivity : AppCompatActivity() {

    // Поля для ввода
    private lateinit var userEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    // Кнопки
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false) // Флаг регистрации
        val hasPinCode = sharedPreferences.getBoolean("hasPinCode", false) // Флаг, есть ли установленный PIN

        if (isRegistered) {
            if (hasPinCode) {
                // Пользователь зарегистрирован и уже имеет PIN-код, перенаправляем на PINActivity
                startActivity(Intent(this, PINActivity::class.java))
                finish()
            }
        }

        setContentView(R.layout.activity_registration)

        // Инициализация полей и кнопок
        userEditText = findViewById(R.id.editTextTextUserName)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextNumberPassword)
        registerButton = findViewById(R.id.button)
        loginButton = findViewById(R.id.button2)

        // Обработка нажатия на кнопку регистрации
        registerButton.setOnClickListener {
            if (validateFields()) {
                val username = userEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                registerUser(username, email, password)
            } else {
                Toast.makeText(this, "Ошибка валидации. Проверьте поля.", Toast.LENGTH_SHORT).show()
            }
        }

        // Переход на экран логина
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    /**
     * Валидация полей ввода
     */
    private fun validateFields(): Boolean {
        var isValid = true

        val user = userEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Проверка имени пользователя
        if (TextUtils.isEmpty(user)) {
            userEditText.error = "Имя пользователя обязательно"
            isValid = false
        } else {
            userEditText.error = null
        }

        // Проверка email
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Введите корректный email"
            isValid = false
        } else {
            emailEditText.error = null
        }

        // Проверка пароля
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Пароль обязателен"
            isValid = false
        } else if (password.length < 8) {
            passwordEditText.error = "Пароль должен быть не менее 8 символов"
            isValid = false
        } else {
            passwordEditText.error = null
        }

        return isValid
    }

    /**
     * Регистрация пользователя
     */
    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {

                // Регистрация пользователя в Supabase
                SupB.getSupB().auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Получение текущего пользователя
                val user = SupB.getSupB().auth.retrieveUserForCurrentSession(updateSession = true)
                Log.e("!!!!!",""+ user.id)
                // Добавление имени пользователя в таблицу
                val userData = mapOf(
                    "id" to user.id,
                    "userName" to username
                )
                SupB.getSupB().postgrest["users"].insert(userData)

                // Переход на следующий экран
                withContext(Dispatchers.Main) {
                    saveRegistrationState()
                    Toast.makeText(this@RegistrationActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegistrationActivity, AdrActivity::class.java))
                }
            } catch (e: Exception) {
                // Обработка ошибок
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrationActivity, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Сохранение состояния регистрации
     */
    private fun saveRegistrationState() {
        val sharedPreferences = getSharedPreferences("SmartHomePrefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isRegistered", true).apply()
    }



}






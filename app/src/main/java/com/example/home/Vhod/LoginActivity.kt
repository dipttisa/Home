package com.example.home.Vhod

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.home.Address.AdrActivity
import com.example.home.MainActivity
import com.example.home.PIN.PINActivity
import com.example.home.PIN.SozdanPINActivity
import com.example.home.R
import com.example.home.sb.SupB
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://wxwopssrwltxpsciktis.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind4d29wc3Nyd2x0eHBzY2lrdGlzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMzMDc2MDUsImV4cCI6MjA0ODg4MzYwNX0.-8vIdnxSZ1BzMO-iTDqPlAWgGnwn2TBsvS2h0bQma94"
    ) {
        install(Postgrest)
    }

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var Button: Button
    private lateinit var Button2: Button
    private lateinit var textView2: TextView
    private lateinit var textView4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false) // Флаг регистрации
        val hasPinCode =
            sharedPreferences.getBoolean("hasPinCode", false) // Флаг, есть ли установленный PIN

        if (isRegistered) {
            if (hasPinCode) {
                // Пользователь зарегистрирован и уже имеет PIN-код, перенаправляем на PINActivity
                startActivity(Intent(this, PINActivity::class.java))
                finish()
            } else {
                // Зарегистрирован, но PIN-код не установлен, перенаправляем на создание PIN-кода
                startActivity(Intent(this, SozdanPINActivity::class.java))
                finish()
            }
        }
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextNumberPassword)
        Button = findViewById(R.id.button)
        Button2 = findViewById(R.id.button2)
        textView4 = findViewById(R.id.textView4)
        textView2 = findViewById(R.id.textView2)

        Button.setOnClickListener {
            if (validateFields()) {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                lifecycleScope.launch {
                    try {
                        SupB.getSupB().auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                            Log.d("DEBUG", "Email: $email, Password: $password")


                        }
                        val user =
                            SupB.getSupB().auth.retrieveUserForCurrentSession(updateSession = true)
                        Log.e("!!!!!", "" + user.id)

                        withContext(Dispatchers.Main) {
                            val sharedPreferences =
                                getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("isRegistered", true).apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Вход выполнен успешно",
                                Toast.LENGTH_SHORT
                            ).show()

                            checkAddressAndRedirect(user.id)


                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("AUTH_ERROR", "Ошибка входа", e)
                            Toast.makeText(
                                this@LoginActivity,
                                "Ошибка входа: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Ошибка валидации", Toast.LENGTH_SHORT).show()
            }
        }


        Button2.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)

        }
    }


    private fun validateFields(): Boolean {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        var isValid = true

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

            Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SozdanPINActivity::class.java)
            startActivity(intent)


        } else {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun checkAddressAndRedirect(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userData = SupB.getSupB().postgrest["users"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<SupB.User>()

                val hasAddress = userData?.address?.isNotEmpty() ?: false

                withContext(Dispatchers.Main) {
                    val sharedPreferences =
                        getSharedPreferences("SmartHomePrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("hasAddress", hasAddress).apply()

                    val intent = when {
                        !sharedPreferences.getBoolean(
                            "hasPinCode",
                            false
                        ) -> Intent(this@LoginActivity, SozdanPINActivity::class.java)

                        !hasAddress -> Intent(this@LoginActivity, AdrActivity::class.java)
                        else -> Intent(this@LoginActivity, MainActivity::class.java)
                    }
                    intent.putExtra("hasAddress", hasAddress)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка проверки адреса: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}




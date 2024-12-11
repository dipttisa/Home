package com.example.home.Address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.home.MainActivity
import com.example.home.R
import com.example.home.sb.SupB
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class AdrActivity : AppCompatActivity() {
    private lateinit var editTextTextPostalAddress: EditText
    private lateinit var button10: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adr)



            editTextTextPostalAddress = findViewById(R.id.editTextTextPostalAddress)
            button10 = findViewById(R.id.button10)



            button10.setOnClickListener {
                val address = editTextTextPostalAddress.text.toString().trim()
                if (validateAddress(address)) {
                    saveAddress(address)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("EXTRA_ADDRESS", address)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Пожалуйста, введите корректный адрес", Toast.LENGTH_SHORT).show()
                }
            }
        }



        private fun validateAddress(address: String): Boolean {
            val addressPattern = Pattern.compile("г\\. [А-Яа-я]+, ул\\. [А-Яа-я]+, д\\. \\d+, кв\\. \\d+")
            return if (address.isEmpty()) {
                editTextTextPostalAddress.error = "Адрес не может быть пустым"
                false
            } else if (!addressPattern.matcher(address).matches()) {
                editTextTextPostalAddress.error = "Неверный формат адреса"
                false
            } else {
                editTextTextPostalAddress.error = null
                true
            }
        }

    private fun saveAddress(address: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = SupB.getSupB().auth.retrieveUserForCurrentSession(updateSession = true)
                    ?: throw Exception("Пользователь не авторизован")

                // Обновление адреса в таблице пользователей
                val userResponse = SupB.getSupB().postgrest["users"]
                    .update({
                        set("address", address)
                    }) {
                        filter {
                            eq("id", user.id)
                        }
                    }

                // Вставка адреса в таблицу houses
                val houseResponse = SupB.getSupB().postgrest["home"]
                    .insert(mapOf(
                        "id_user" to user.id,
                        "address" to address
                    ))



                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdrActivity, "Адрес успешно сохранен", Toast.LENGTH_SHORT).show()
                    getSharedPreferences("SmartHomePrefs", MODE_PRIVATE).edit().putBoolean("hasAddress", true).apply()
                    startActivity(Intent(this@AdrActivity, MainActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdrActivity, "Ошибка сохранения адреса: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }



        }




    }




}
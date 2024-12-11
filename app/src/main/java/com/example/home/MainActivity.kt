package com.example.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.home.CtoTo.AddRoomActivity
import com.example.home.Fragments.DevicesFragment
import com.example.home.Fragments.RoomFragment
import com.example.home.Fragments.UserFragment
import com.example.home.sb.SupB
import com.example.home.Vhod.LoginActivity
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewRooms: TextView
    private lateinit var textViewDevices: TextView
    private lateinit var textViewUsers: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Скрываем заголовок ActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        textViewRooms = findViewById(R.id.textViewRooms)
        textViewDevices = findViewById(R.id.textViewDevices)
        textViewUsers = findViewById(R.id.textViewUsers)



        textViewRooms.setOnClickListener { showFragment(RoomFragment()) }
        textViewDevices.setOnClickListener { showFragment(DevicesFragment()) }
        textViewUsers.setOnClickListener { showFragment(UserFragment()) }


        // Создание LinearLayout для размещения текста "Твой дом" и адреса
        val linearLayout = android.widget.LinearLayout(this)
        linearLayout.orientation = android.widget.LinearLayout.VERTICAL
        linearLayout.gravity = android.view.Gravity.START

        // Создание TextView для текста "Твой дом"
        val titleTextView = TextView(this)
        titleTextView.text = "Твой дом"
        titleTextView.textSize = 30f
        titleTextView.setTextColor(resources.getColor(R.color.white))

        // Создание TextView для адреса
        val addressTextView = TextView(this)
        addressTextView.textSize = 16f
        addressTextView.setTextColor(resources.getColor(R.color.gray))

        // Добавление TextView в LinearLayout
        linearLayout.addView(titleTextView)
        linearLayout.addView(addressTextView)

        // Установка параметров для LinearLayout
        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = android.view.Gravity.START
        linearLayout.layoutParams = layoutParams

        // Добавление LinearLayout в Toolbar
        toolbar.addView(linearLayout)

        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
        progressBar.visibility = View.GONE
        linearLayout.addView(progressBar)

        // Проверка авторизации пользователя
        if (!isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        showFragment(RoomFragment())

        // Получаем адрес текущего пользователя
        getCurrentUserAddress(addressTextView)
    }

    private fun isUserLoggedIn(): Boolean {
        return SupB.getSupB().auth.currentSessionOrNull() != null
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }


    private fun getCurrentUserAddress(addressTextView: TextView) {
        addressTextView.text = "Загрузка адреса..."


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = SupB.getSupB().auth.retrieveUserForCurrentSession(updateSession = true)
                    ?: throw Exception("Пользователь не авторизован")

                val userData = SupB.getSupB().postgrest["users"]
                    .select {
                        filter {
                            eq("id", user.id)
                        }
                    }
                    .decodeSingleOrNull<SupB.User>()

                val address = userData?.address

                withContext(Dispatchers.Main) {
                    if (!address.isNullOrEmpty()) {
                        addressTextView.text = address
                    } else {
                        addressTextView.text = "Адрес не указан"
                    }
                }
            } catch (e: Exception) {
                Log.e("!!!!", "Ошибка получения адреса: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Ошибка получения адреса: ${e.message}", Toast.LENGTH_SHORT).show()
                    addressTextView.text = "Ошибка получения адреса"
                }
            }
        }
    }
}
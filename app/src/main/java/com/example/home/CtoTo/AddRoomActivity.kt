package com.example.home.CtoTo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.home.Adapters.RoomCardAdapter
import com.example.home.Fragments.RoomFragment
import com.example.home.R
import com.example.home.sb.SupB
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AddRoomActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomCardAdapter
    private lateinit var roomNameEditText: EditText
    private lateinit var addRoomButton: Button
    private lateinit var roomTypesList: MutableList<SupB.RoomType>

    private fun navigateToFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = RoomFragment()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)

        // Инициализация элементов интерфейса
        roomNameEditText = findViewById(R.id.roomNameEditText)
        addRoomButton = findViewById(R.id.addRoomButton)
        roomTypesList = mutableListOf()
        recyclerView = findViewById(R.id.roomsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val imageButtonBack: AppCompatImageButton = findViewById(R.id.imageButtonBack)
        imageButtonBack.setOnClickListener {
            navigateToFragment()
        }

        // Инициализация адаптера
        adapter = RoomCardAdapter(roomTypesList) { roomTypeName ->
            // Обработка выбора типа комнаты
            Toast.makeText(this, "Выбран тип комнаты: $roomTypeName", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Загрузка данных из базы
        loadRoomTypes()

        // Обработка нажатия кнопки "Добавить комнату"
        addRoomButton.setOnClickListener {
            saveRoom()
        }
    }

    private fun loadRoomTypes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Получаем список типов комнат из базы
                val roomTypesResult = SupB.getSupB().postgrest["room_type"]
                    .select()
                    .decodeList<SupB.RoomType>()

                Log.d("AddRoomActivity", "Загружено типов комнат: ${roomTypesResult.size}")

                // Обновляем список данных и адаптер на главном потоке
                withContext(Dispatchers.Main) {
                    roomTypesList.clear()
                    roomTypesList.addAll(roomTypesResult)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // Обработка ошибок
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddRoomActivity,
                        "Ошибка загрузки типов комнат: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("AddRoomActivity", "Ошибка загрузки типов комнат", e)
                }
            }
        }
    }

    private fun saveRoom() {
        val roomName = roomNameEditText.text.toString().trim()

        if (roomName.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Получаем выбранный тип комнаты
                    val selectedRoomType = roomTypesList[adapter.selectedPosition]

                    // Получаем текущего пользователя
                    val user =
                        SupB.getSupB().auth.retrieveUserForCurrentSession(updateSession = true)
                            ?: throw Exception("Пользователь не авторизован")

                    // Получаем дом пользователя или создаём новый
                    val home = SupB.getSupB().postgrest["home"]
                        .select {
                            filter {
                                eq("id_user", user.id)
                            }
                        }
                        .decodeList<SupB.Home>()

                    val homeId = if (home.isNotEmpty()) {
                        home.first().id
                    } else {
                        val newHome = SupB.Home(
                            id = UUID.randomUUID().toString(),
                            id_user = user.id,
                            address = "Default Address"
                        )
                        SupB.getSupB().postgrest["home"].insert(newHome)
                            .decodeSingle<SupB.Home>().id
                    }

                    if (homeId != null) {
                        // Создаём комнату
                        val room = SupB.Room(
                            id = UUID.randomUUID().toString(),
                            name = roomName,
                            room_type_id = selectedRoomType.id,
                            home_id = homeId,
                            image_room = selectedRoomType.image,
                            image_ho = selectedRoomType.image_room
                        )

                        SupB.getSupB().postgrest["room"].insert(room)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddRoomActivity,
                                "Комната добавлена",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Отправляем результат обратно в фрагмент, чтобы обновить список
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddRoomActivity,
                                "Ошибка при создании дома",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddRoomActivity,
                            "Ошибка при добавлении комнаты: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("AddRoomActivity", "Ошибка при добавлении комнаты", e)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Название комнаты не может быть пустым", Toast.LENGTH_SHORT).show()
        }
    }
}

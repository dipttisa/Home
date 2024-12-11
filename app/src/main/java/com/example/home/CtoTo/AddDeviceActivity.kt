package com.example.home.CtoTo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.home.Adapters.DeviceCardAdapter
import com.example.home.Fragments.DevicesFragment
import com.example.home.R
import com.example.home.sb.SupB
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceCardAdapter
    private lateinit var deviceNameEditText: EditText
    private lateinit var identydeviceNameEditText: EditText
    private lateinit var roomSpinner: Spinner
    private lateinit var addDeviceButton: Button
    private lateinit var deviceTypesList: MutableList<SupB.DeviceType>
    private lateinit var roomList: MutableList<SupB.Room>

    private fun navigateToFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, DevicesFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        // Инициализация View
        deviceNameEditText = findViewById(R.id.deviceNameEditText)
        identydeviceNameEditText = findViewById(R.id.identydeviceNameEditText)
        roomSpinner = findViewById(R.id.roomSpinner)
        addDeviceButton = findViewById(R.id.addDeviceButton)
        recyclerView = findViewById(R.id.deviceRecyclerView)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val imageButtonBack: AppCompatImageButton = findViewById(R.id.imageButtonBack)
        imageButtonBack.setOnClickListener {
            navigateToFragment()
        }

        // Инициализация списков
        deviceTypesList = mutableListOf()
        roomList = mutableListOf()

        // Установка адаптера для RecyclerView
        adapter = DeviceCardAdapter(deviceTypesList) { deviceTypeName ->
            Toast.makeText(this, "Выбран тип устройства: $deviceTypeName", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Загрузка данных из базы
        loadDeviceTypes()
        loadRooms()

        // Обработка нажатия кнопки "Добавить устройство"
        addDeviceButton.setOnClickListener {
            saveDevice()
        }
    }

    private fun loadDeviceTypes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceTypesResult = SupB.getSupB().postgrest["device_type"]
                    .select()
                    .decodeList<SupB.DeviceType>()

                Log.d("AddDeviceActivity", "Загружено типов устройств: ${deviceTypesResult.size}")

                withContext(Dispatchers.Main) {
                    deviceTypesList.clear()
                    deviceTypesList.addAll(deviceTypesResult)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddDeviceActivity, "Ошибка загрузки типов устройств: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddDeviceActivity", "Ошибка загрузки типов устройств", e)
                }
            }
        }
    }

    private fun loadRooms() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val roomsResult = SupB.getSupB().postgrest["room"]
                    .select()
                    .decodeList<SupB.Room>()

                Log.d("AddDeviceActivity", "Загружено комнат: ${roomsResult.size}")

                withContext(Dispatchers.Main) {
                    roomList.clear()
                    roomList.addAll(roomsResult)

                    val roomNames = roomList.map { it.name }
                    val spinnerAdapter = ArrayAdapter(
                        this@AddDeviceActivity,
                        android.R.layout.simple_spinner_item,
                        roomNames
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    roomSpinner.adapter = spinnerAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddDeviceActivity, "Ошибка загрузки комнат: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddDeviceActivity", "Ошибка загрузки комнат", e)
                }
            }
        }
    }

    private fun saveDevice() {
        val deviceName = deviceNameEditText.text.toString().trim()
        val deviceId = identydeviceNameEditText.text.toString().trim()

        if (deviceName.isEmpty()) {
            Toast.makeText(this, "Название устройства не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        if (deviceId.isEmpty()) {
            Toast.makeText(this, "ID устройства не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (adapter.selectedPosition < 0 || roomSpinner.selectedItemPosition < 0) {
                    throw Exception("Необходимо выбрать тип устройства и комнату")
                }

                val selectedDeviceType = deviceTypesList[adapter.selectedPosition]
                val selectedRoom = roomList[roomSpinner.selectedItemPosition]

                val device = SupB.Device(
                    id = UUID.randomUUID().toString(),
                    name = deviceName,
                    device_type_id = selectedDeviceType.id,
                    room_id = selectedRoom.id,
                    device_id = deviceId,
                    isOn = false, // Предполагаем, что устройство изначально выключено
                    image_device_type = selectedDeviceType.image_device_type,
                    image_device = selectedDeviceType.image_device,
                    roomName = selectedRoom.name
                )

                SupB.getSupB().postgrest["device"].insert(device)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddDeviceActivity, "Устройство добавлено", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddDeviceActivity, "Ошибка при добавлении устройства: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddDeviceActivity", "Ошибка при добавлении устройства", e)
                }
            }
        }
    }
}

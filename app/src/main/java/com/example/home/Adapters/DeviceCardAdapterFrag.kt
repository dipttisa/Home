package com.example.home.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.home.R
import com.example.home.sb.SupB
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceCardAdapterFrag(
    private val deviceTypes: List<SupB.Device>, // Список устройств
    private val onDeviceSelected: (String) -> Unit // Обработчик выбора устройства
) : RecyclerView.Adapter<DeviceCardAdapterFrag.DeviceViewHolder>() {

    // Храним текущее состояние устройств
    private val deviceStates: MutableMap<String, Boolean> = mutableMapOf()

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceImageView: ImageView = itemView.findViewById(R.id.deviceImageView)
        private val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
        private val deviceSwitch: Switch = itemView.findViewById(R.id.deviceSwitch)

        fun bind(device: SupB.Device) {
            // Устанавливаем название устройства
            deviceNameTextView.text = device.name

            // Загружаем изображение устройства с помощью Picasso
            Picasso.get()
                .load(device.image_device) // URL изображения устройства
                .error(R.drawable.error_image) // Если не удалось загрузить изображение
                .into(deviceImageView)

            // Устанавливаем текущее состояние Switch из deviceStates или по умолчанию
            deviceSwitch.isChecked = deviceStates[device.id] ?: device.isOn

            // Устанавливаем слушатель для переключателя
            deviceSwitch.setOnCheckedChangeListener { _, isChecked ->
                deviceStates[device.id] = isChecked // Обновляем состояние в map
                device.isOn = isChecked // Обновляем модель устройства
                updateDeviceStatus(device) // Сохраняем новое состояние устройства
                onDeviceSelected("${device.name} is ${if (isChecked) "ON" else "OFF"}")
            }

            // Обработчик нажатия на устройство (карту устройства)
            itemView.setOnClickListener {
                onDeviceSelected(device.name) // Передаем название устройства в коллбек
            }
        }

        private fun updateDeviceStatus(device: SupB.Device) {
            // Обновляем состояние устройства на сервере или в базе данных
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = SupB.getSupB().postgrest["device"]
                        .update({
                            set("isOn", device.isOn)
                        }) {
                            filter {
                                eq("id", device.id)
                            }
                        }

                    if (response.data != null && response.data.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            Log.d("DeviceUpdate", "Устройство успешно обновлено: ${device.name}")
                            Snackbar.make(itemView, "Устройство обновлено", Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.d("DeviceUpdate", "Устройство успешно обновлено: ${device.name}")
                            Snackbar.make(itemView, "Устройство обновлено", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Snackbar.make(itemView, "Ошибка: ${e.message}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = deviceTypes[position]
        holder.bind(device)
    }

    override fun getItemCount(): Int {
        return deviceTypes.size
    }

    // Метод для получения состояния устройства
    fun getDeviceState(deviceId: String): Boolean {
        return deviceStates[deviceId] ?: false
    }




}

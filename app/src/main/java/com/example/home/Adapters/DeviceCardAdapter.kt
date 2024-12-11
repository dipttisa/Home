package com.example.home.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.service.controls.DeviceTypes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.home.R
import com.example.home.sb.SupB
import com.squareup.picasso.Picasso

class DeviceCardAdapter (
    private val deviceTypes: List<SupB.DeviceType>,
    private val onDeviceTypeSelected: (String) -> Unit
) : RecyclerView.Adapter<DeviceCardAdapter.DeviceViewHolder>() {
    var selectedPosition = 0
    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(deviceType: SupB.DeviceType, isSelected: Boolean) {
            val deviceImageView: ImageView = itemView.findViewById(R.id.deviceImageView) // Фон картинки
            val deviceImageView2: ImageView = itemView.findViewById(R.id.deviceImageView2) // Картинка из базы
            val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)

            // Установка названия комнаты
            deviceNameTextView.text = deviceType.name

            // Логирование данных
            Log.d("DeviceCardAdapter", "Binding DeviceType: Name = ${deviceType.name}, Image = ${deviceType.image_device_type}")

            // Загрузка изображения из базы (изображение всегда белое)
            Picasso.get()
                .load(deviceType.image_device_type) // URL изображения
                .placeholder(ColorDrawable(Color.LTGRAY)) // Заглушка
                .error(R.drawable.error_image) // Ошибка загрузки
                .into(deviceImageView2)


            deviceImageView.clipToOutline = true
            deviceImageView.outlineProvider = CircleOutlineProvider()
            // Изменение цвета фона для roomImageView в зависимости от состояния
            deviceImageView.setBackgroundColor(
                if (isSelected) Color.parseColor("#0B50A0") else Color.GRAY
            )

            // Установка цвета текста
            deviceNameTextView.setTextColor(
                if (isSelected) Color.parseColor("#0B50A0") else Color.GRAY
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val deviceType = deviceTypes[position]
        holder.bind(deviceType, position == selectedPosition)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onDeviceTypeSelected(deviceType.name) // Передаём id выбранного типа комнаты
        }
    }

    override fun getItemCount(): Int {
        return deviceTypes.size
    }
}
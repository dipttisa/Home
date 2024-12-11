package com.example.home.Adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

class RoomCardAdapter(
    private val roomTypes: List<SupB.RoomType>,
    private val onRoomTypeSelected: (String) -> Unit
) : RecyclerView.Adapter<RoomCardAdapter.RoomViewHolder>() {

    var selectedPosition = 0

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(roomType: SupB.RoomType, isSelected: Boolean) {
            val roomImageView: ImageView = itemView.findViewById(R.id.roomImageView) // Фон картинки
            val roomImageView2: ImageView = itemView.findViewById(R.id.roomImageView2) // Картинка из базы
            val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)

            // Установка названия комнаты
            roomNameTextView.text = roomType.name

            // Логирование данных
            Log.d("RoomCardAdapter", "Binding RoomType: Name = ${roomType.name}, Image = ${roomType.image}")

            // Загрузка изображения из базы (изображение всегда белое)
            Picasso.get()
                .load(roomType.image) // URL изображения
                .placeholder(ColorDrawable(Color.LTGRAY)) // Заглушка
                .error(R.drawable.error_image) // Ошибка загрузки
                .into(roomImageView2)


            roomImageView.clipToOutline = true
            roomImageView.outlineProvider = CircleOutlineProvider()
            // Изменение цвета фона для roomImageView в зависимости от состояния
            roomImageView.setBackgroundColor(
                if (isSelected) Color.parseColor("#0B50A0") else Color.GRAY
            )

            // Установка цвета текста
            roomNameTextView.setTextColor(
                if (isSelected) Color.parseColor("#0B50A0") else Color.GRAY
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val roomType = roomTypes[position]
        holder.bind(roomType, position == selectedPosition)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onRoomTypeSelected(roomType.name) // Передаём id выбранного типа комнаты
        }
    }

    override fun getItemCount(): Int {
        return roomTypes.size
    }
}

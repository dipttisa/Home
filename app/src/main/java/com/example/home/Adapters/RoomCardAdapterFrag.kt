package com.example.home.Adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.home.R
import com.example.home.sb.SupB
import com.squareup.picasso.Picasso

class RoomCardAdapterFrag(
    private val roomTypes: List<SupB.Room>, // Список комнат
    private val onRoomSelected: (String) -> Unit // Обработчик выбора комнаты
) : RecyclerView.Adapter<RoomCardAdapterFrag.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomImageView: ImageView = itemView.findViewById(R.id.roomImageView)
        private val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)

        fun bind(room: SupB.Room) {
            // Устанавливаем название комнаты
            roomNameTextView.text = room.name

            Picasso.get()
                .load(room.image_ho) // URL изображения
                .error(R.drawable.error_image) // Если не удалось загрузить изображение
                .into(roomImageView)

            // Обработчик нажатия на комнату
            itemView.setOnClickListener {
                onRoomSelected(room.name) // Передаем название комнаты в коллбек
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomTypes[position]
        holder.bind(room)
    }



    override fun getItemCount(): Int {
        return roomTypes.size
    }
}

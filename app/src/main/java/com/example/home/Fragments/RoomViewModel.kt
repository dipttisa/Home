package com.example.home.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.home.sb.SupB
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomViewModel : ViewModel() {
    val roomList = MutableLiveData<MutableList<SupB.Room>>(mutableListOf())

    fun loadRooms() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val rooms = SupB.getSupB().postgrest["room"].select().decodeList<SupB.Room>()
                withContext(Dispatchers.Main) {
                    // Обновляем LiveData с новым списком
                    roomList.value = rooms.toMutableList()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Обработка ошибки
                    Log.e("RoomViewModel", "Ошибка загрузки комнат", e)
                }
            }
        }
    }
}

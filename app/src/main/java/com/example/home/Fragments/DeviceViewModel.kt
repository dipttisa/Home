package com.example.home.Fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.home.sb.SupB
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceViewModel: ViewModel() {
    val deviceList = MutableLiveData<MutableList<SupB.Device>>(mutableListOf())

    fun loadDevice() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val device = SupB.getSupB().postgrest["device"].select().decodeList<SupB.Device>()
                withContext(Dispatchers.Main) {
                    // Обновляем LiveData с новым списком
                    deviceList.value = device.toMutableList()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Обработка ошибки
                    Log.e("DeviceViewModel", "Ошибка загрузки устройств", e)
                }
            }
        }
    }
}

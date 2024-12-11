package com.example.home.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.home.Adapters.DeviceCardAdapterFrag
import com.example.home.CtoTo.AddDeviceActivity
import com.example.home.R


class DevicesFragment : Fragment() {

    private lateinit var addDeviceButton: AppCompatImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceCardAdapterFrag
    private lateinit var viewModel: DeviceViewModel


    // Используем Activity Result API для запуска AddDeviceActivity
    private val addDeviceResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Перезагружаем список устройств после успешного добавления
            viewModel.loadDevice()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_devices, container, false)

        viewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

        recyclerView = rootView.findViewById(R.id.deviceRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Наблюдаем за deviceList
        viewModel.deviceList.observe(viewLifecycleOwner, Observer { devices ->
            if (devices.isNotEmpty()) {
                // Обновляем адаптер, если данные есть
                adapter = DeviceCardAdapterFrag(devices) { deviceName ->
                    Toast.makeText(requireContext(), "Вы выбрали устройство: $deviceName", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
            }
        })

        // Кнопка для добавления устройства
        addDeviceButton = rootView.findViewById(R.id.addDeviceButton)
        addDeviceButton.setOnClickListener { navigateToAddDeviceActivity() }

        // Загружаем устройства
        viewModel.loadDevice()

        return rootView
    }

    private fun navigateToAddDeviceActivity() {
        val intent = Intent(activity, AddDeviceActivity::class.java)
        addDeviceResultLauncher.launch(intent)
    }
}

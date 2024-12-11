package com.example.home.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.home.Adapters.RoomCardAdapterFrag
import com.example.home.CtoTo.AddRoomActivity
import com.example.home.R

import com.example.home.ViewModels.RoomViewModel

class RoomFragment : Fragment() {

    private lateinit var addRoomButton: AppCompatImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomCardAdapterFrag
    private lateinit var viewModel: RoomViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_room, container, false)

        // Получаем ViewModel
        viewModel = ViewModelProvider(this).get(RoomViewModel::class.java)

        recyclerView = rootView.findViewById(R.id.roomsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Наблюдаем за roomList
        viewModel.roomList.observe(viewLifecycleOwner, Observer { rooms ->
            if (rooms.isNotEmpty()) {
                // Если данные есть, обновляем адаптер
                adapter = RoomCardAdapterFrag(rooms) { roomName ->
                    Toast.makeText(requireContext(), "Вы выбрали комнату: $roomName", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
            }
        })

        // Кнопка для добавления комнаты
        addRoomButton = rootView.findViewById(R.id.addRoomButton)
        addRoomButton.setOnClickListener { navigateToActivity() }

        // Загружаем комнаты
        viewModel.loadRooms()

        return rootView
    }

    private fun navigateToActivity() {
        val intent = Intent(activity, AddRoomActivity::class.java)
        startActivityForResult(intent, ADD_ROOM_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_ROOM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Перезагружаем список комнат после добавления новой
            viewModel.loadRooms()
        }
    }


    companion object {
        const val ADD_ROOM_REQUEST_CODE = 1
    }
}

package com.example.home.sb

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import kotlinx.serialization.Serializable

object SupB {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://wxwopssrwltxpsciktis.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind4d29wc3Nyd2x0eHBzY2lrdGlzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMzMDc2MDUsImV4cCI6MjA0ODg4MzYwNX0.-8vIdnxSZ1BzMO-iTDqPlAWgGnwn2TBsvS2h0bQma94"    ) {
        install(Postgrest)
        install(Auth)
    }
    public fun getSupB(): SupabaseClient{
        return supabase
    }



    @Serializable
    data class User(
        val id: String,
        val userName: String,
        val address: String
    )


    @Serializable
    data class Home(
        val id: String,
        val address: String,
        val id_user: String
    )
    @Serializable
    data class RoomType(
        val id: String,
        val name: String,
        val image: String,
        val image_room: String

    )

    @Serializable
    data class Room(
        val id: String,
        val name: String,
        val room_type_id: String,
        val home_id: String,
        val image_room: String, // Added this field
        val image_ho: String // Added this field
    )
    @Serializable
    data class UserFamily(
        val id: String,
        val home_id: String,
        val username: String,
        val email: String,
        val password: String,
        val image_user: String
    )

    @Serializable
    data class DeviceType(
        val id: String,
        val name: String,
        val image_device_type: String,
        val image_device: String

    )

    @Serializable
    data class Device(
        val id: String,
        val name: String,
        val room_id: String,
        val device_id: String,
        val device_type_id: String,
        var isOn: Boolean,
        var roomName: String,
        val image_device_type: String,
        val image_device: String

    )

}
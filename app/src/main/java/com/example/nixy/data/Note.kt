package com.example.nixy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey
    val id: Int = 1,
    val title: String = "",
    val description: String = ""
)
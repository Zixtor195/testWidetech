package com.example.testwidetech.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfoDB (
    @PrimaryKey
    val userEmail: String,
    val provider: String
    )
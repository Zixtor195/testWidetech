package com.example.testwidetech.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = arrayOf(UserInfoDB::class), version = 1)
abstract class DataBaseRoom : RoomDatabase() {

    abstract fun DaoRoom(): DaoRoom
}
package com.example.testwidetech.db

import androidx.room.*

@Dao
interface DaoRoom {

    @Query("SELECT * FROM UserInfoDB WHERE userEmail = :userEmail")
    fun getUserById(userEmail:String): UserInfoDB

    @Update
    fun update(userInfoDB: UserInfoDB)

    @Insert
    fun addUser(userInfoDB: UserInfoDB)

    @Delete
    fun deleteUser(userInfoDB: UserInfoDB)
}
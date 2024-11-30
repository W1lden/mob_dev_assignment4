package com.example.assignment4.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user_table")
    fun getAll(): List<User>
}

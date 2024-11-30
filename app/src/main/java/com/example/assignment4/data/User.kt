package com.example.assignment4.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table") // Add @Entity annotation
data class User(
    @PrimaryKey(autoGenerate = true) // Add @PrimaryKey annotation
    val id: Int = 0,

    @ColumnInfo(name = "name") // Optional column name
    val name: String,

    @ColumnInfo(name = "email") // Optional column name
    val email: String
)

package com.androidstudio_2024_vision.learnflow.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes"
)
data class NoteEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    val userId:Long,

    val videoTitle: String,

    val noteText: String,

    val position: Long,

    val imagePath: String? = null
)
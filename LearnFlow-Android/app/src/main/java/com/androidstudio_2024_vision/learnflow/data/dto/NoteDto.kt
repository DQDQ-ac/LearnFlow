package com.androidstudio_2024_vision.learnflow.data.dto

data class NoteDto(

    val id: Long? = null,

    val userId: Long,

    val videoTitle: String,

    val noteText: String,

    val position: Long,

    val imagePath: String? = null
)
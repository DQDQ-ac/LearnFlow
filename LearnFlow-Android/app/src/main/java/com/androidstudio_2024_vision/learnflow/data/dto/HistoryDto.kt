package com.androidstudio_2024_vision.learnflow.data.dto

data class HistoryDto(

    val id: Long? = null,

    val userId: Long,

    val videoTitle: String,

    val videoUrl: String,

    val position: Long,

    val updateTime: Long
)
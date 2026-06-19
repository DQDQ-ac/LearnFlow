package com.androidstudio_2024_vision.learnflow.data.dto

data class TaskDto(
    val taskId: Long? = null,
    val userId: Long,
    val title: String,
    val completed: Boolean,
    val date: String,
    val priority: Int
)
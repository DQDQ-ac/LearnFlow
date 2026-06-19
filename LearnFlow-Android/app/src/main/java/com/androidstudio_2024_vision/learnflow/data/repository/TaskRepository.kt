package com.androidstudio_2024_vision.learnflow.data.repository

import com.androidstudio_2024_vision.learnflow.data.dto.TaskDto
import com.androidstudio_2024_vision.learnflow.data.room.TaskDao
import com.androidstudio_2024_vision.learnflow.data.room.TaskEntity
import com.androidstudio_2024_vision.learnflow.network.ApiService

class TaskRepository(
    private val api: ApiService
) {

    suspend fun addTask(task: TaskDto) {
        api.addTask(task)
    }

    suspend fun getTasks(userId: Long): List<TaskDto> {
        return api.getTasks(userId)
    }

    suspend fun updateTask(task: TaskDto) {
        api.updateTask(task)
    }

    suspend fun deleteTask(id: Long) {
        api.deleteTask(id)
    }
}
package com.androidstudio_2024_vision.learnflow.data.repository

import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.room.HistoryDao
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.network.ApiService

class HistoryRepository(
    private val api: ApiService
) {

    suspend fun saveHistory(
        history: HistoryDto
    ) {
        api.saveHistory(history)
    }

    suspend fun getLatestHistory(
        userId: Long
    ): HistoryDto? {

        val result =
            api.getLatestHistory(userId)

        println("服务器返回=$result")

        return result
    }

    suspend fun getHistoryList(
        userId: Long
    ): List<HistoryDto> {

        return api.getHistoryList(userId)
    }

    suspend fun deleteHistory(
        id: Long
    ) {
        api.deleteHistory(id)
    }

}
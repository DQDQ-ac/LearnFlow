package com.androidstudio_2024_vision.learnflow.ui.study

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.data.repository.HistoryRepository
import com.androidstudio_2024_vision.learnflow.data.repository.NoteRepository
import com.androidstudio_2024_vision.learnflow.data.room.AppDatabase
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.data.room.StudyHeatMap
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudyViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val noteRepo =
        NoteRepository(
            RetrofitClient.api
        )

    private val historyRepo =
        HistoryRepository(
            RetrofitClient.api
        )

    private val _uiState =
        MutableStateFlow(
            StudyUiState()
        )

    val uiState: StateFlow<StudyUiState> = _uiState

    val loginManager =
        LoginManager(application)



    init {

        viewModelScope.launch {

            val userId =
                loginManager
                    .userFlow
                    .first()
                    .userId

            observeData(userId)
        }

    }


    private fun observeData(userId: Long) {
        viewModelScope.launch {
            try {
                val noteCount = noteRepo.getNoteCount(userId).toInt() // 笔记数量

                val historyList = historyRepo.getHistoryList(userId) // 所有历史记录 DTO
                val latestHistory = historyList.maxByOrNull { it.updateTime } // 最新历史

                // ===========================
                // 统计数据（前端计算）
                // ===========================
                val totalStudyTime = historyList.sumOf { it.position } // 总学习时长（毫秒）

                val courseCount = historyList.map { it.videoTitle }.distinct().count() // 学过课程数

                val studyDays = historyList.map {
                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = it.updateTime
                    cal.get(java.util.Calendar.DAY_OF_YEAR) to cal.get(java.util.Calendar.YEAR)
                }.distinct().count() // 不同天的数量

                val heatMap = RetrofitClient.api.getLast7DaysHeatMap(userId)

                _uiState.value = StudyUiState(
                    totalStudyTime = totalStudyTime,
                    noteCount = noteCount,
                    courseCount = courseCount,
                    studyDays = studyDays,
                    heatMap = heatMap,
                    latestHistory = latestHistory
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
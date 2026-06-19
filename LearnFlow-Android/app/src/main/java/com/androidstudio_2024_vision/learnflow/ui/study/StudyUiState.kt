package com.androidstudio_2024_vision.learnflow.ui.study

import com.androidstudio_2024_vision.learnflow.data.dto.HeatMapDto
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.data.room.StudyHeatMap

data class StudyUiState(

    val totalStudyTime: Long = 0,

    val noteCount: Int = 0,

    val courseCount: Int = 0,
    val studyDays: Int = 0,
    val heatMap: List<HeatMapDto> = emptyList(),
    val latestHistory: HistoryDto? = null
)
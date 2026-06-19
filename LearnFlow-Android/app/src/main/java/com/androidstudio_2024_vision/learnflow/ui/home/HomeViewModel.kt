package com.androidstudio_2024_vision.learnflow.ui.home

import android.app.Application
import android.media.MediaMetadataRetriever
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.androidstudio_2024_vision.learnflow.data.course.Course
import com.androidstudio_2024_vision.learnflow.data.course.CourseRepository
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.androidstudio_2024_vision.learnflow.data.repository.HistoryRepository
import com.androidstudio_2024_vision.learnflow.data.room.AppDatabase
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import com.androidstudio_2024_vision.learnflow.ui.study.StudyUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = CourseRepository()

    private val historyRepository =
        HistoryRepository(RetrofitClient.api)

    private val _courses = MutableStateFlow<List<Course>>(emptyList())

    val courses: StateFlow<List<Course>> = _courses

    private val _latestHistory = MutableStateFlow<HistoryDto?>(null)

    val latestHistory: StateFlow<HistoryDto?> = _latestHistory

    private val loginManager = LoginManager(application)

    private var userId = -1L

    init {

        viewModelScope.launch {

            userId =
                loginManager
                    .userFlow
                    .first()
                    .userId

            observeHistory()
        }

        loadCourses(application)
    }

    private fun loadCourses(application: Application) {
        viewModelScope.launch {
            val list = repo.getCourses(application)

            list.forEach { course ->
                try {
                    // 去掉 asset:/// 前缀
                    val assetPath = course.videoUrl.removePrefix("asset:///")
                    val afd = application.assets.openFd(assetPath)

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

                    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0L

                    val sec = durationMs / 1000
                    val min = sec / 60
                    val remain = sec % 60

                    course.duration = "%02d:%02d".format(min, remain)

                    retriever.release()
                    afd.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    course.duration = "00:00"
                }
            }

            _courses.value = list
        }
    }

    private fun observeHistory() {
        if (userId < 0) return

        viewModelScope.launch {
            try {
                val latest =
                    historyRepository.getLatestHistory(userId)
                println("首页历史记录=$latest")
                _latestHistory.value = latest

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
package com.androidstudio_2024_vision.learnflow.ui.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.data.dto.TaskDto
import com.androidstudio_2024_vision.learnflow.data.repository.TaskRepository
import com.androidstudio_2024_vision.learnflow.data.room.AppDatabase
import com.androidstudio_2024_vision.learnflow.data.room.TaskEntity
import com.androidstudio_2024_vision.learnflow.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class TaskViewModel(
    application: Application,
    private val api: ApiService
) : AndroidViewModel(application) {

    private val repo = TaskRepository(api)

    private var userId: Long = -1L

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks

    // ✅ 完成率（保留）
    val completionRate: StateFlow<Int> =
        _tasks
            .map { list ->
                if (list.isEmpty()) 0
                else list.count { it.completed } * 100 / list.size
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0
            )
    private val loginManager =
        LoginManager(application)

    init {
        viewModelScope.launch {
            userId =
                loginManager
                    .userFlow
                    .first()
                    .userId
            loadTasks()
        }
    }

    // =========================
    // 🔵 拉取任务（来自后端）
    // =========================
    private fun loadTasks() {
        viewModelScope.launch {
            try {

                val list = repo.getTasks(userId)

                println("任务数量=${list.size}")

                list.forEach {
                    println(it)
                }

                _tasks.value = list

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // =========================
    // 🟢 添加任务（POST）
    // =========================
    fun addTask(title: String, date: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            repo.addTask(
                TaskDto(
                    userId = userId,
                    title = title,
                    completed = false,
                    date = date,
                    priority = 1
                )
            )

            loadTasks() // 刷新列表
        }
    }

    // =========================
    // 🟡 切换完成状态（PUT）
    // =========================
    fun toggleTask(task: TaskDto) {
        viewModelScope.launch {
            repo.updateTask(
                task.copy(completed = !task.completed)
            )
            loadTasks()
        }
    }

    // =========================
    // 🔴 删除任务
    // =========================
    fun deleteTask(task: TaskDto) {
        val id = task.taskId ?: return // 如果没 id 就直接返回，不删
        viewModelScope.launch {
            repo.deleteTask(id)
            loadTasks()
        }
    }

    // =========================
    // 🔵 手动刷新
    // =========================
    fun refresh() {
        loadTasks()
    }
}
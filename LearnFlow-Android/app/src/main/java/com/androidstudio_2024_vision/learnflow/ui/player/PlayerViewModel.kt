package com.androidstudio_2024_vision.learnflow.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidstudio_2024_vision.learnflow.data.repository.NoteRepository
import com.androidstudio_2024_vision.learnflow.data.room.AppDatabase
import com.androidstudio_2024_vision.learnflow.data.room.NoteEntity
import com.androidstudio_2024_vision.learnflow.player.ExoPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.androidstudio_2024_vision.learnflow.data.repository.HistoryRepository
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import android.graphics.Bitmap
import android.os.Environment
import androidx.media3.ui.PlayerView
import java.io.File
import java.io.FileOutputStream
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.data.dto.HeatMapDto
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn


class PlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val playerManager = ExoPlayerManager(application)
    private val loginManager = LoginManager(application)

    val player = playerManager.player

    private var pendingSeek: Long? = null

    private val repository =
        NoteRepository(
            RetrofitClient.api
        )

    private val historyRepository =
        HistoryRepository(RetrofitClient.api)

    private var username = ""
    private var userId = -1L

    private val _uiState =
        MutableStateFlow(
            PlayerUiState()
        )

    val uiState: StateFlow<PlayerUiState> = _uiState
    private var currentVideoTitle = ""

    private var currentVideoUrl = ""

    private var lastUpdate = System.currentTimeMillis()


    private var heatJob: Job? = null

    fun startHeatMapTracking() {
        if (heatJob != null) return

        heatJob = viewModelScope.launch {
            val userId = loginManager.userFlow.first().userId
            var last = System.currentTimeMillis()

            while (true) {
                delay(1000)

                val now = System.currentTimeMillis()
                val delta = ((now - last) / 1000).toInt().coerceAtLeast(0)
                last = now

                RetrofitClient.api.addHeatMapDuration(userId, delta)
            }
        }
    }

    fun stopHeatMapTracking() {
        heatJob?.cancel()
        heatJob = null
    }

    fun getLast7DaysHeatMap(userId: Long): StateFlow<List<HeatMapDto>> =
        flow {
            emit(RetrofitClient.api.getLast7DaysHeatMap(userId))
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList<HeatMapDto>()
        )

    init {

        player.playWhenReady = true



        player.addListener(

            object :

                androidx.media3.common
                .Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {

                    if (state == androidx.media3.common.Player.STATE_READY) {

                        pendingSeek
                            ?.let { player.seekTo(it)
                                pendingSeek = null
                            }
                    }
                }
            }
        )
        viewModelScope.launch {
            userId =
                loginManager
                    .userFlow
                    .first()
                    .userId

            loginManager.userFlow.collect {

                userId = it.userId

                username = it.username

            }
        }

        startProgressUpdate()
    }
    fun loadVideo(uri: Uri) {
        currentVideoTitle= uri.lastPathSegment ?:"unknown"

        currentVideoUrl = uri.toString()

        playerManager.setVideo(uri.toString())

        player.play()
        loadNotes()

        _uiState.value =
            _uiState.value.copy(
                currentVideoUri = uri,
                currentPosition = 0L,
                notes = emptyList()
            )
    }
    private fun observeHistory() {
        if (userId < 0) return

        viewModelScope.launch {
            try {
                val latest = historyRepository.getLatestHistory(userId)
                _uiState.value = _uiState.value.copy(
                    history = latest // 这里类型是 HistoryDto
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadNotes() {

        viewModelScope.launch {

            val notes =

                repository
                    .getNotes(userId)

            _uiState.value =
                _uiState.value.copy(
                    notes = notes.filter {
                            it.videoTitle == currentVideoTitle
                        }
                )
        }
    }

    private fun startProgressUpdate() {

        viewModelScope.launch {

            while (true) {

                _uiState.value =
                    _uiState.value.copy(

                        isPlaying = player.isPlaying,

                        currentPosition = player.currentPosition
                    )

                val state = _uiState.value

                if (state.isLooping) {

                    val start = state.loopStart

                    val end = state.loopEnd

                    if (start != null && end != null && player.currentPosition >= end) {
                        player.seekTo(start)
                    }
                }

                if (currentVideoTitle.isNotBlank() && player.currentPosition > 0) {

                    autoSaveHistory()
                }

                delay(500)
            }
        }
    }

    //支持系统课程:
    fun loadRemoteVideo(
        url: String,
        title: String
    ) {

        currentVideoTitle = title
        currentVideoUrl = url
        playerManager.setVideo(url)

        player.play()

        loadNotes()

        _uiState.value =
            _uiState.value.copy(
                currentPosition = 0L,
                notes = emptyList(),
                currentVideoUri =
                    Uri.parse(url)
            )
    }
    fun updateNoteInput(text: String) {

        _uiState.value = _uiState.value.copy(noteInput = text)
    }
    fun togglePlay() {

        if (player.isPlaying)
            player.pause()
        else
            player.play()
    }

    fun setSpeed(speed: Float) {

        playerManager.setSpeed(speed)

        _uiState.value =
            _uiState.value.copy(speed = speed)
    }

    fun addNote() {
        if (userId < 0) return

        if (_uiState.value.noteInput.isBlank()) return
        viewModelScope.launch {

            repository.addNote(

                NoteDto(

                    userId = userId,

                    videoTitle = currentVideoTitle,

                    noteText = _uiState.value.noteInput,

                    position = player.currentPosition,

                    imagePath = null
                )
            )
            loadNotes()

            _uiState.value =
                _uiState.value.copy(
                    noteInput = ""
                )
        }
    }

    fun deleteNote(
        note: NoteDto
    ) {

        viewModelScope.launch {

            // 删除本地截图文件
            note.imagePath?.let {

                val file = java.io.File(it)

                if (file.exists()) {
                    file.delete()
                }
            }

            // 删除数据库记录
            note.id?.let {

                repository.deleteNote(it)
            }

            // 刷新列表
            loadNotes()
        }
    }

    fun seekTo(position: Long) {

        pendingSeek = position

        if (
            player.playbackState == androidx.media3.common.Player.STATE_READY
        ) {

            player.seekTo(position)

            pendingSeek = null
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {

        if (userId < 0) return

        val file = File(

            getApplication<Application>()
                .filesDir,

            "note_${
                System.currentTimeMillis()
            }.jpg"
        )

        FileOutputStream(file).use {

            bitmap.compress(

                Bitmap.CompressFormat.JPEG,

                90,

                it
            )
        }

        viewModelScope.launch {

            repository.addNote(

                NoteDto(

                    userId = userId,

                    videoTitle = currentVideoTitle,

                    noteText =

                    if (
                        _uiState.value
                            .noteInput
                            .isBlank()
                    )

                        "截图重点"

                    else

                        _uiState.value
                            .noteInput,

                    position =
                    player.currentPosition,

                    imagePath =
                    file.absolutePath
                )
            )

            _uiState.value =
                _uiState.value.copy(
                    noteInput = ""
                )

            loadNotes()
        }
    }

    @androidx.media3.common.util.UnstableApi
    fun captureFrame(
        playerView: PlayerView
    ) {

        val surfaceView = playerView.videoSurfaceView as? android.view.SurfaceView ?: return

        val bitmap =

            Bitmap.createBitmap(

                surfaceView.width,

                surfaceView.height,

                Bitmap.Config.ARGB_8888
            )

        PixelCopy.request(

            surfaceView,

            bitmap,

            {

                if (

                    it ==
                    PixelCopy.SUCCESS
                ) {

                    saveBitmap(
                        bitmap
                    )
                }
            },

            Handler(
                Looper
                    .getMainLooper()
            )
        )
    }

//A点
    fun setLoopStart() {

        _uiState.value =
            _uiState.value.copy(

                loopStart =
                player.currentPosition
            )
    }
//B点
    fun setLoopEnd() {

        _uiState.value =
            _uiState.value.copy(

                loopEnd =
                player.currentPosition
            )
    }

    fun startLoop() {

        if (_uiState.value.loopStart == null || _uiState.value.loopEnd == null) return

        _uiState.value = _uiState.value.copy(isLooping = true)
    }

    fun stopLoop() {

        _uiState.value = _uiState.value.copy(isLooping = false)
    }

    //保存：课程，URL，进度，时间
    fun saveHistory() {
        if (userId < 0 || currentVideoTitle.isBlank()) return

        viewModelScope.launch {
            try {
                historyRepository.saveHistory(
                    HistoryDto(
                        userId = userId,
                        videoTitle = currentVideoTitle,
                        videoUrl = currentVideoUrl,
                        position = player.currentPosition,
                        updateTime = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var lastHistorySave = 0L

    private fun autoSaveHistory() {

        val now = System.currentTimeMillis()

        if (now - lastHistorySave < 30000)
            return

        lastHistorySave = now

        viewModelScope.launch {

            historyRepository.saveHistory(
                HistoryDto(
                    userId = userId,
                    videoTitle = currentVideoTitle,
                    videoUrl = currentVideoUrl,
                    position = player.currentPosition,
                    updateTime = now
                )
            )
        }
    }

    override fun onCleared() {
        playerManager.release()
    }
}
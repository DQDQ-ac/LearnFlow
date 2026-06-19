package com.androidstudio_2024_vision.learnflow.ui.player

import android.net.Uri
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.data.room.NoteEntity

//作用： 管理： 播放器： UI状态
data class PlayerUiState(

    val isPlaying: Boolean = false,

    val speed: Float = 1f,

    val currentPosition: Long = 0L,

    //UI：能感知Room 笔记
    val notes: List<NoteDto> =
        emptyList(),
    //UI知道当前播放哪个视频
    val currentVideoUri: Uri? = null,
    val noteInput: String = "",
//AB点循环
    val loopStart: Long? = null,
    val loopEnd: Long? = null,
    val isLooping: Boolean = false,

    val history: HistoryDto? = null
)
package com.androidstudio_2024_vision.learnflow.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "history"
)
data class HistoryEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    val userId:Long,//用户

    val videoTitle: String,//课程

    val videoUrl: String,//恢复播放

    val position: Long,//播放位置

    val updateTime: Long//最近学习排序
)
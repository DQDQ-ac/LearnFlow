package com.androidstudio_2024_vision.learnflow.data.datastore

data class UserSession(

    val userId: Long = -1,

    val username: String = "",

    val nickname: String = "",

    val isLogin: Boolean = false
)
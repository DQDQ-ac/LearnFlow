package com.androidstudio_2024_vision.learnflow.network

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String
)
package com.androidstudio_2024_vision.learnflow.network

data class ApiResponse<T>(

    val code:Int,

    val message:String,

    val data:T?
)
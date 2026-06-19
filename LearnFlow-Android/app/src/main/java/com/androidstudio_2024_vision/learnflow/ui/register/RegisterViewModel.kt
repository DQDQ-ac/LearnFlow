package com.androidstudio_2024_vision.learnflow.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidstudio_2024_vision.learnflow.network.ApiResponse
import com.androidstudio_2024_vision.learnflow.network.RegisterRequest
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
//ViewModel 负责请求数据、保存状态

class RegisterViewModel : ViewModel() {

    private val _registerResult =
        MutableStateFlow<ApiResponse<Any>?>(null)

    val registerResult =
        _registerResult

    fun register(
        username: String,
        password: String,
        nickname: String
    ) {

        viewModelScope.launch {

            try {

                val result =
                    RetrofitClient
                        .api
                        .register(
                            RegisterRequest(
                                username,
                                password,
                                nickname
                            )
                        )

                _registerResult.value =
                    result

            } catch (e: Exception) {

                e.printStackTrace()

                _registerResult.value =
                    ApiResponse(
                        code = -1,
                        message = e.message ?: "网络异常",
                        data = null
                    )
            }
        }
    }
}
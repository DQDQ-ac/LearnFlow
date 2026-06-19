package com.androidstudio_2024_vision.learnflow.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidstudio_2024_vision.learnflow.network.ApiResponse
import com.androidstudio_2024_vision.learnflow.network.LoginRequest
import com.androidstudio_2024_vision.learnflow.network.LoginResponse
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult =
        MutableStateFlow<ApiResponse<LoginResponse>?>(null)

    val loginResult =
        _loginResult

    fun login(
        username: String,
        password: String
    ) {
        viewModelScope.launch {

            try {

                println("开始登录：$username")

                val result =
                    RetrofitClient
                        .api
                        .login(
                            LoginRequest(
                                username,
                                password
                            )
                        )

                println("登录结果：$result")

                _loginResult.value = result

            } catch (e: Exception) {

                e.printStackTrace()

                println("登录异常：${e.message}")

                _loginResult.value =
                    ApiResponse(
                        code = -1,
                        message = e.message ?: "网络异常",
                        data = null
                    )
            }
        }
    }
}
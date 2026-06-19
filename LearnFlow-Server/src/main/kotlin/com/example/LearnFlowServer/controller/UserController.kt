package com.example.LearnFlowServer.controller

import com.example.LearnFlowServer.dto.ApiResponse
import com.example.LearnFlowServer.dto.RegisterRequest
import com.example.LearnFlowServer.repository.UserRepository
import com.example.LearnFlowServer.dto.LoginRequest
import com.example.LearnFlowServer.dto.LoginResponse
import com.example.LearnFlowServer.entity.UserEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(

    private val userRepository:
    UserRepository
) {

    @PostMapping("/register")
    fun register(

        @RequestBody
        request: RegisterRequest

    ): ApiResponse<Any> {

        val exist =
            userRepository
                .findByUsername(
                    request.username
                )

        if (exist != null) {

            return ApiResponse(
                409,
                "用户名已存在"
            )
        }

        userRepository.save(

            UserEntity(

                username =
                    request.username,

                password =
                    request.password,

                nickname =
                    request.nickname
            )
        )

        return ApiResponse(
            200,
            "注册成功"
        )
    }

    @PostMapping("/login")
    fun login(

        @RequestBody
        request: LoginRequest

    ): ApiResponse<LoginResponse> {

        val user =

            userRepository
                .findByUsername(
                    request.username
                )
        println("收到登录请求：${request.username}")

        if (user == null) {

            return ApiResponse(
                code = 404,
                message = "用户不存在"
            )
        }

        if (user.password != request.password) {

            return ApiResponse(
                code = 401,
                message = "密码错误"
            )
        }

        return ApiResponse(

            code = 200,

            message = "登录成功",

            data = LoginResponse(

                userId = user.id,

                username = user.username,

                nickname = user.nickname
            )
        )
    }
}
package com.androidstudio_2024_vision.learnflow.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var errorText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val loginManager = remember { LoginManager(context) }
    val vm: RegisterViewModel = viewModel()
    val registerResult by vm.registerResult.collectAsState()

    LaunchedEffect(registerResult) {
        registerResult?.let {
            isLoading = false
            when (it.code) {
                200 -> {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
                409 -> errorText = "用户名已存在"
                -1 -> errorText = "网络异常"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 注册图标（和登录区分：登录用School，注册用PersonAdd）
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = Color(0xFF5A3EBF),
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "注册账号",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5A3EBF)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "加入LearnFlow开始学习",
            fontSize = 13.sp,
            color = Color(0xFF999999)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 用户名
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorText = ""
            },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 密码
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorText = ""
            },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(
                    onClick = { showPassword = !showPassword },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (showPassword) "隐藏" else "显示",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 确认密码
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorText = ""
            },
            label = { Text("确认密码") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        // 错误提示
        if (errorText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorText,
                fontSize = 12.sp,
                color = Color(0xFFE53935),
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 注册按钮
        Button(
            onClick = {
                errorText = ""
                when {
                    username.isBlank() -> errorText = "请输入用户名"
                    username.length < 3 -> errorText = "用户名至少3个字符"
                    password.isBlank() -> errorText = "请输入密码"
                    password.length < 6 -> errorText = "密码至少6个字符"
                    password != confirmPassword -> errorText = "两次输入的密码不一致"
                    else -> {
                        isLoading = true
                        vm.register(username, password, username)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("注册", fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 返回登录入口
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                text = "已有账号？去登录",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
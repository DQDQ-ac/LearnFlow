package com.androidstudio_2024_vision.learnflow.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val loginManager = remember { LoginManager(context) }
    val vm: LoginViewModel = viewModel()
    val loginResult by vm.loginResult.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(loginResult) {
        loginResult?.let { response ->
            isLoading = false
            when (response.code) {
                200 -> {
                    loginManager.saveUser(
                        userId = response.data!!.userId,
                        username = response.data.username,
                        nickname = response.data.nickname
                    )
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                401 -> errorText = "密码错误"
                404 -> errorText = "用户不存在"
                -1 -> errorText = "网络异常"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // =======================
        // 🔵 顶部插画/图标区域
        // =======================
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = Color(0xFF5A3EBF),
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Logo
        Text(
            text = "LearnFlow",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5A3EBF)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "视频学习助手",
            fontSize = 13.sp,
            color = Color(0xFF888888)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // =======================
        // 输入区
        // =======================
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorText = "" },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorText = "" },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (errorText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorText,
                fontSize = 12.sp,
                color = Color(0xFFE53935),
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                when {
                    username.isBlank() -> errorText = "请输入用户名"
                    password.isBlank() -> errorText = "请输入密码"
                    else -> {
                        isLoading = true
                        vm.login(username, password)
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
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("登录", fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text(
                "没有账号？去注册",
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
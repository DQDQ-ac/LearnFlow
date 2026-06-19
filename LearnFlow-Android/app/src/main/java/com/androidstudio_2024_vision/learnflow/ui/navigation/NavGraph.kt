package com.androidstudio_2024_vision.learnflow.ui.navigation

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.network.RetrofitClient
import com.androidstudio_2024_vision.learnflow.ui.home.HomeViewModel
import com.androidstudio_2024_vision.learnflow.ui.login.LoginScreen
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerScreen
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerViewModel
import com.androidstudio_2024_vision.learnflow.ui.register.RegisterScreen
import com.androidstudio_2024_vision.learnflow.ui.study.StudyViewModel
import com.androidstudio_2024_vision.learnflow.ui.task.TaskScreen
import com.androidstudio_2024_vision.learnflow.ui.task.TaskViewModel

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val loginManager = remember { LoginManager(context) }
    val application = context.applicationContext as Application

    val playerVm: PlayerViewModel = viewModel(
        factory = viewModelFactory {
            initializer { PlayerViewModel(application) }
        }
    )

    val homeVm: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HomeViewModel(application) }
        }
    )

    val studyVm: StudyViewModel = viewModel(
        factory = viewModelFactory {
            initializer { StudyViewModel(application) }
        }
    )

    val taskVm: TaskViewModel = viewModel(
        factory = viewModelFactory {
            initializer { TaskViewModel(application, RetrofitClient.api) }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {

            LaunchedEffect(Unit) {

                val user = loginManager.userFlow.first()

                if (user.username.isNotBlank()) {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("main") {
            MainScreen(
                playerVm = playerVm,
                homeVm = homeVm,
                taskVm = taskVm,
                rootNavController = navController
            )
        }
        composable("player") {
            PlayerScreen(playerVm)
        }
    }
}
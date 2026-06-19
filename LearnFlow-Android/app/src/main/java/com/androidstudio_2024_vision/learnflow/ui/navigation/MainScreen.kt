package com.androidstudio_2024_vision.learnflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import com.androidstudio_2024_vision.learnflow.ui.home.HomeScreen
import com.androidstudio_2024_vision.learnflow.ui.profile.ProfileScreen
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidstudio_2024_vision.learnflow.ui.home.HomeViewModel
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerViewModel
import com.androidstudio_2024_vision.learnflow.ui.study.StudyViewModel
import com.androidstudio_2024_vision.learnflow.ui.task.TaskScreen
import com.androidstudio_2024_vision.learnflow.ui.task.TaskViewModel


@Composable
fun MainScreen(playerVm: PlayerViewModel,homeVm:HomeViewModel,taskVm: TaskViewModel,rootNavController: NavController) {

    val navController = rememberNavController()

    val studyVm:
            StudyViewModel = viewModel()

    val items = listOf(
        "home",
        "player",
        "task",
        "profile"
    )

    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                    },
                    icon = {
                        Icon(Icons.Default.Home, null)
                    },
                    label = {
                        Text("首页")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("player")
                    },
                    icon = {
                        Icon(Icons.Default.PlayArrow, null)
                    },
                    label = {
                        Text("学习")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("task")
                    },
                    icon = {
                        Icon(Icons.Default.DateRange, null)
                    },
                    label = {
                        Text("任务")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("profile")
                    },
                    icon = {
                        Icon(Icons.Default.Person, null)
                    },
                    label = {
                        Text("我的")
                    }
                )
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)  // ✅ 添加这一行
        ) {

            composable("home") {
                HomeScreen(navController, playerVm, homeVm)
            }

            composable("player") {
                PlayerScreen(vm = playerVm)
            }

            composable("task") {
                TaskScreen(taskVm)
            }

            composable("profile") {
                ProfileScreen(
                    vm = studyVm,
                    navController = rootNavController
                )
            }
        }
    }
}
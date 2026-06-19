package com.androidstudio_2024_vision.learnflow.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerViewModel
import com.androidstudio_2024_vision.learnflow.ui.player.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    playerVm: PlayerViewModel,
    homeVm: HomeViewModel
) {

    val courses by homeVm.courses.collectAsState()
    val history by homeVm.latestHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "系统课程",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ===== 最近学习 =====
            history?.let {

                item {
                    Text(
                        "继续学习",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Text(
                                it.videoTitle,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "上次看到 ${formatTime(it.position)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    playerVm.loadRemoteVideo(
                                        it.videoUrl,
                                        it.videoTitle
                                    )
                                    playerVm.seekTo(it.position)
                                    navController.navigate("player")
                                }
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("继续学习")
                            }
                        }
                    }
                }
            }

            // ===== 课程区标题 =====
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "课程列表",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ===== 课程列表 =====
            items(courses) { course ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            playerVm.loadRemoteVideo(
                                course.videoUrl,
                                course.title
                            )
                            navController.navigate("player")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                course.title,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "时长 ${course.duration}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                            )
                        }

                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
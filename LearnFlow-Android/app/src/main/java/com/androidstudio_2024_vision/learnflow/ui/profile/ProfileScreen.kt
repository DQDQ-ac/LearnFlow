package com.androidstudio_2024_vision.learnflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.androidstudio_2024_vision.learnflow.data.datastore.LoginManager
import com.androidstudio_2024_vision.learnflow.data.dto.HeatMapDto
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.room.HistoryEntity
import com.androidstudio_2024_vision.learnflow.data.room.StudyHeatMap
import com.androidstudio_2024_vision.learnflow.ui.player.PlayerViewModel
import com.androidstudio_2024_vision.learnflow.ui.player.formatTime
import com.androidstudio_2024_vision.learnflow.ui.study.StudyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: StudyViewModel = viewModel(),
    navController: NavController
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loginManager = remember { LoginManager(context) }

    val playerVm: PlayerViewModel = viewModel()

    var username by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    val userId = loginManager.userFlow.collectAsState(initial = null).value?.userId ?: 0L




    LaunchedEffect(Unit) {
        loginManager.userFlow.collectLatest {
            username = it.username
            nickname = it.nickname
        }
    }

    val totalSeconds = uiState.totalStudyTime / 1000
    val totalHours = totalSeconds / 3600f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null)
                        Spacer(Modifier.width(6.dp))
                        Text("学习中心", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(16.dp))
            UserHeader(nickname, username)
            Spacer(Modifier.height(24.dp))

            StudyTimeHero(totalHours)
            Spacer(Modifier.height(24.dp))

            ContinueLearningCard(
                latest = uiState.latestHistory,
                onContinue = { history ->
                    try {
                        playerVm.loadRemoteVideo(
                            history.videoUrl,
                            history.videoTitle
                        )
                        playerVm.seekTo(history.position)
                        navController.navigate("player")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
            Spacer(Modifier.height(24.dp))

            SecondaryStats(
                noteCount = uiState.noteCount,
                courseCount = uiState.courseCount,
                studyDays = uiState.studyDays
            )
            Spacer(Modifier.height(24.dp))

            // ✅ 传入 HeatMapDto 列表
            HeatMapBlock(data = uiState.heatMap)
            Spacer(Modifier.height(32.dp))

            TextButton(
                onClick = {
                    scope.launch {
                        loginManager.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(Modifier.width(6.dp))
                Text("退出登录", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserHeader(nickname: String, username: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(50.dp) // 略微放大头像
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nickname.firstOrNull()?.uppercase() ?: "?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                nickname.ifBlank { "未登录用户" },
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (username.isNotBlank()) {
                Text(
                    "@$username",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }
        }
    }
}

@Composable
fun StudyTimeHero(totalHours: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text("累计学习", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "%.1f".format(totalHours),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "小时",
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ContinueLearningCard(
    latest: HistoryDto?,
    onContinue: (HistoryDto) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { latest?.let(onContinue) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text("继续学习", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(10.dp))

            if (latest != null) {
                Text(
                    latest.videoTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "进度 ${formatTime(latest.position)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            } else {
                Text(
                    "暂无学习记录",
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        }
    }
}

@Composable
fun SecondaryStats(
    noteCount: Int,
    courseCount: Int,
    studyDays: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        MiniStat("重点", noteCount.toString(), Modifier.weight(1f))
        MiniStat("课程", courseCount.toString(), Modifier.weight(1f))
        MiniStat("天数", (studyDays+2).toString(), Modifier.weight(1f))
    }
}

@Composable
fun MiniStat(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

@Composable
fun HeatMapBlock(data: List<HeatMapDto>) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("近7天学习热力", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        }
        Text(
            "深色表示学习时间更长",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        )
        Spacer(Modifier.height(12.dp))

        val calendar = java.util.Calendar.getInstance()

        val last7Days = (0..6).map {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -(6 - it))
            val year = cal.get(java.util.Calendar.YEAR)
            val month = cal.get(java.util.Calendar.MONTH) + 1
            val day = cal.get(java.util.Calendar.DAY_OF_MONTH)

            String.format("%04d-%02d-%02d", year, month, day)
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            last7Days.forEach { day ->
                // 安全获取学习时长
                val duration = data.find { it.day == day }?.totalDuration ?: 0

                // 渐变颜色（调试阶段可见）
                val color = when {
                    duration <= 0 -> MaterialTheme.colorScheme.surfaceVariant
                    duration < 60 -> MaterialTheme.colorScheme.primary.copy(0.25f)   // <1分钟
                    duration < 300 -> MaterialTheme.colorScheme.primary.copy(0.5f)    // <5分钟
                    duration < 1800 -> MaterialTheme.colorScheme.primary.copy(0.75f)  // <30分钟
                    else -> MaterialTheme.colorScheme.primary
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                    Spacer(Modifier.height(4.dp))
                    // 只显示 MM-dd
                    val label = day.split("-").let { if (it.size == 3) "${it[1]}-${it[2]}" else day }
                    Text(label, fontSize = 10.sp)
                    if (duration > 0) Text("${duration / 60}min", fontSize = 10.sp)
                }
            }
        }
    }
}
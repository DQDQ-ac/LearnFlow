package com.androidstudio_2024_vision.learnflow.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(vm: TaskViewModel) {

    val tasks by vm.tasks.collectAsState()
    val rate by vm.completionRate.collectAsState()

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val todo = tasks.filter { !it.completed }
    val done = tasks.filter { it.completed }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ===== 标题 =====
        item {
            Text(
                "学习任务",
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
        }

        // ===== 完成率卡片 =====
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
                        "完成进度",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$rate%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { rate / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ===== 输入任务 =====
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("任务内容") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (date.isBlank()) "选择截止日期" else "截止：$date")
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    vm.addTask(title, date)
                    title = ""
                    date = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("添加任务")
            }
            Spacer(Modifier.height(16.dp))
        }

        // ===== 待完成任务 =====
        if (todo.isNotEmpty()) {
            item {
                Text(
                    "待完成",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(todo) { TaskItem(it, vm) }
        }

        // ===== 已完成任务 =====
        if (done.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "已完成",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(done) { TaskItem(it, vm) }
        }
    }

    // ===== 日期选择器 =====
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            date = formatter.format(Date(it))
                        }
                        showDatePicker = false
                    }
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
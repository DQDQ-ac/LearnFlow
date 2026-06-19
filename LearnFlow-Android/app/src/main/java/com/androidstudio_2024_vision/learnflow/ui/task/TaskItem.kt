package com.androidstudio_2024_vision.learnflow.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.androidstudio_2024_vision.learnflow.data.dto.TaskDto

@Composable
fun TaskItem(

    task: TaskDto,

    vm: TaskViewModel
) {

    var showDialog by
    remember {

        mutableStateOf(false)
    }

    Card(

        modifier =
        Modifier
            .fillMaxWidth()
            .padding(
                vertical = 6.dp
            )
    ) {

        Row(

            modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    12.dp
                ),

            verticalAlignment =
            Alignment.CenterVertically,

            horizontalArrangement =
            Arrangement.SpaceBetween
        ) {

            Row(

                verticalAlignment =
                Alignment.CenterVertically
            ) {

                Checkbox(

                    checked =
                    task.completed,

                    onCheckedChange = {

                        vm.toggleTask(
                            task
                        )
                    }
                )

                Column {

                    Text(

                        task.title,

                        textDecoration =

                        if (
                            task.completed
                        )

                            TextDecoration
                                .LineThrough

                        else null
                    )

                    if (
                        task.date
                            .isNotBlank()
                    ) {

                        Text(

                            task.date,

                            style =
                            MaterialTheme
                                .typography
                                .bodySmall
                        )
                    }
                }
            }

            IconButton(

                onClick = {

                    showDialog =
                        true
                }
            ) {

                Icon(

                    Icons.Default.Delete,

                    null
                )
            }
        }
    }

    if (
        showDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showDialog =
                    false
            },

            title = {

                Text(
                    "删除任务"
                )
            },

            text = {

                Text(
                    "确认删除该任务？"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        vm.deleteTask(
                            task
                        )

                        showDialog =
                            false
                    }
                ) {

                    Text("删除")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDialog =
                            false
                    }
                ) {

                    Text("取消")
                }
            }
        )
    }
}
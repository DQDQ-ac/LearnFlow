package com.androidstudio_2024_vision.learnflow.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import java.io.File
import android.graphics.BitmapFactory

@Composable
fun NoteTimelineItem(
    note: NoteDto,
    onSeek: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (() -> Unit)? = null  // 可选，如果不传则不显示编辑按钮
) {
    var showImageDialog by remember { mutableStateOf(false) }
    var currentImagePath by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 第一行：时间点和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 时间点（可点击跳转）
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSeek() },
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = formatTime(note.position),
                        fontSize = 12.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // 操作按钮组
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 编辑按钮（如果有编辑功能）
                    onEdit?.let {
                        IconButton(
                            onClick = it,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "编辑",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    // 删除按钮
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 第二行：笔记内容
            if (note.noteText.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = note.noteText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 第三行：截图（如果有）
            note.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    Spacer(Modifier.height(8.dp))

                    val bitmap = remember(path) {
                        BitmapFactory.decodeFile(path)
                    }

                    bitmap?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F0F0))
                                .clickable {
                                    currentImagePath = path
                                    showImageDialog = true
                                }
                        ) {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "截图",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // 放大图标提示
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "🔍 点击放大",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 图片放大查看弹窗
    if (showImageDialog && currentImagePath != null) {
        val bitmap = remember(currentImagePath) {
            currentImagePath?.let { BitmapFactory.decodeFile(it) }
        }

        if (bitmap != null) {
            FullScreenImageDialog(
                bitmap = bitmap,
                onDismiss = {
                    showImageDialog = false
                    currentImagePath = null
                }
            )
        }
    }
}

@Composable
fun FullScreenImageDialog(
    bitmap: android.graphics.Bitmap,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() }
        ) {
            // 图片
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "截图放大",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan
                            // 限制偏移范围
                            offset = Offset(
                                x = offset.x.coerceIn(-500f, 500f),
                                y = offset.y.coerceIn(-500f, 500f)
                            )
                        }
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
                contentScale = ContentScale.Fit
            )

            // 关闭按钮
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Color.White
                )
            }

            // 底部提示
            Text(
                text = "双指缩放 | 拖动查看 | 点击关闭",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
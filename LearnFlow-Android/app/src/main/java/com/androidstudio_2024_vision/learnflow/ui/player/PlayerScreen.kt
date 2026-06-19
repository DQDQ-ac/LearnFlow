package com.androidstudio_2024_vision.learnflow.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import java.io.File
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.input.pointer.PointerEventType

@androidx.media3.common.util.UnstableApi
@Composable
fun PlayerScreen(vm: PlayerViewModel) {

    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    var playerView by remember { mutableStateOf<PlayerView?>(null) }

    // 编辑笔记相关状态
    var editingNote by remember { mutableStateOf<NoteDto?>(null) }
    var editText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    // 删除确认弹框
    var deletingNote by remember { mutableStateOf<NoteDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 图片放大查看状态
    var imageToView by remember { mutableStateOf<String?>(null) }

    // 文件选择器
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            vm.loadVideo(it)
        }
    }

    DisposableEffect(uiState.currentVideoUri) {
        onDispose {
            vm.stopHeatMapTracking()
            vm.saveHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "学习播放器",
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp
        )
        Spacer(Modifier.height(12.dp))

        StyledButton(
            text = "导入学习视频",
            onClick = { launcher.launch(arrayOf("video/*")) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LaunchedEffect(uiState.currentVideoUri) {
            vm.startHeatMapTracking()
        }

        // 播放器视图
        if (uiState.currentVideoUri != null) {
            AndroidView(
                factory = { ctx -> PlayerView(ctx).also { playerView = it } },
                update = { it.player = vm.player },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("请先导入视频")
            }
        }

        Spacer(Modifier.height(16.dp))

        // 播放进度
        Text("播放进度：${formatTime(uiState.currentPosition)}", fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))

        // 播放控制 + 倍速
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledButton(
                text = if (uiState.isPlaying) "暂停" else "播放",
                onClick = { vm.togglePlay() },
                modifier = Modifier.weight(1f)
            )
            StyledButton(text = "1x", onClick = { vm.setSpeed(1f) }, modifier = Modifier.weight(1f))
            StyledButton(text = "1.5x", onClick = { vm.setSpeed(1.5f) }, modifier = Modifier.weight(1f))
            StyledButton(text = "2x", onClick = { vm.setSpeed(2f) }, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // A-B 循环
        Text("A-B循环", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledButton(text = "A点", onClick = { vm.setLoopStart() }, modifier = Modifier.weight(1f))
            StyledButton(text = "B点", onClick = { vm.setLoopEnd() }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StyledButton(text = "开始", onClick = { vm.startLoop() }, modifier = Modifier.weight(1f))
            StyledButton(text = "停止", onClick = { vm.stopLoop() }, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(6.dp))
        Text(
            "A: ${uiState.loopStart?.let { formatTime(it) } ?: "--"}  " +
                    "B: ${uiState.loopEnd?.let { formatTime(it) } ?: "--"}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )

        Spacer(Modifier.height(16.dp))

        // 输入笔记
        OutlinedTextField(
            value = uiState.noteInput,
            onValueChange = { vm.updateNoteInput(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("输入重点内容") }
        )
        Spacer(Modifier.height(8.dp))

        StyledButton(text = "⭐ 记重点", onClick = { vm.addNote() }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(12.dp))
        StyledButton(
            text = "📸 截图重点",
            onClick = { playerView?.let { vm.captureFrame(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // 学习重点列表
        Text("学习重点", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        uiState.notes.forEach { note ->
            NoteItemCard(
                note = note,
                onSeek = { vm.seekTo(note.position) },
                onEdit = {
                    editingNote = note
                    editText = note.noteText
                    showEditDialog = true
                },
                onDelete = {
                    deletingNote = note
                    showDeleteDialog = true
                },
                onImageClick = { imagePath ->
                    imageToView = imagePath
                }
            )
        }
    }

    // 编辑笔记弹框
    if (showEditDialog && editingNote != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("编辑重点") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    label = { Text("重点内容") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editText.isNotBlank()) {
                            // 更新笔记
                            val updatedNote = editingNote!!.copy(noteText = editText)
//                            // 调用更新方法
//                            vm.updateNote(updatedNote)
                        }
                        showEditDialog = false
                        editingNote = null
                        editText = ""
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                    editingNote = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // 删除确认弹框
    if (showDeleteDialog && deletingNote != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除重点") },
            text = { Text("确定要删除这条重点吗？删除后无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteNote(deletingNote!!)
                        showDeleteDialog = false
                        deletingNote = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deletingNote = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // 图片放大查看弹框
    if (imageToView != null) {
        FullScreenImageDialog(
            imagePath = imageToView!!,
            onDismiss = { imageToView = null }
        )
    }
}

@Composable
fun NoteItemCard(
    note: NoteDto,
    onSeek: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onImageClick: (String) -> Unit
) {
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
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // 操作按钮组
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 编辑按钮
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
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
                                .clickable { onImageClick(path) }
                        ) {
                            androidx.compose.foundation.Image(
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
}

@Composable
fun FullScreenImageDialog(
    imagePath: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val bitmap = remember(imagePath) {
        BitmapFactory.decodeFile(imagePath)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() }
        ) {
            if (bitmap != null) {
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
                                offset = Offset(
                                    x = offset.x.coerceIn(-500f, 500f),
                                    y = offset.y.coerceIn(-500f, 500f)
                                )
                            }
                        }
                        .pointerInput(Unit) {
                            // 鼠标滚轮缩放（模拟器可用）
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Scroll) {
                                        val scrollDelta = event.changes.first().scrollDelta
                                        val delta = scrollDelta.y / 100f
                                        scale = (scale + delta).coerceIn(1f, 5f)
                                    }
                                }
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
            }

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
                text = "鼠标滚轮缩放 | 拖动查看 | 点击关闭",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun StyledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}
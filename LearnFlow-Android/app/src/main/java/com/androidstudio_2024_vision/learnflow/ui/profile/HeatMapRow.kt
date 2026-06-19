package com.androidstudio_2024_vision.learnflow.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.androidstudio_2024_vision.learnflow.data.room.StudyHeatMap

@Composable
fun HeatMapCell(count: Int) {
    val alpha = when {
        count >= 3600 -> 1f
        count >= 1800 -> 0.7f
        count >= 60 -> 0.4f
        else -> 0.15f
    }
    Box(
        Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}
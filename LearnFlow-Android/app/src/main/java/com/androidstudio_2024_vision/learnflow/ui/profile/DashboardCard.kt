package com.androidstudio_2024_vision.learnflow.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardCard(

    title: String,

    value: String

) {

    Card(

        modifier =
        Modifier
            .fillMaxWidth()
            .padding(
                vertical = 6.dp
            )
    ) {

        Column(

            modifier =
            Modifier.padding(
                16.dp
            )
        ) {

            Text(
                title
            )

            Spacer(
                Modifier.height(
                    8.dp
                )
            )

            Text(

                value,

                style =
                MaterialTheme
                    .typography
                    .headlineSmall
            )
        }
    }
}
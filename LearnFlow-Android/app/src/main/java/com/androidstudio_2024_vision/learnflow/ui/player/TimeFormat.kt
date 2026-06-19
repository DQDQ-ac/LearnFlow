package com.androidstudio_2024_vision.learnflow.ui.player

fun formatTime(

    ms: Long
): String {

    val totalSec =
        ms / 1000

    val min =
        totalSec / 60

    val sec =
        totalSec % 60

    return "%02d:%02d".format(min, sec)
}
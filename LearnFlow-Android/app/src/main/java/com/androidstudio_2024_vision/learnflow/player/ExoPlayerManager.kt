package com.androidstudio_2024_vision.learnflow.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager(
    private val context: Context
) {

    val player: ExoPlayer =
        ExoPlayer.Builder(context).build()

    fun setVideo(url: String) {

        val realUri =

            if (url.startsWith("asset:///")) {

                Uri.parse(
                    "file:///android_asset/" +
                            url.removePrefix("asset:///")
                )

            } else {

                Uri.parse(url)
            }

        player.setMediaItem(
            MediaItem.fromUri(realUri)
        )

        player.prepare()
    }

    fun setSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun release() {
        player.release()
    }
}
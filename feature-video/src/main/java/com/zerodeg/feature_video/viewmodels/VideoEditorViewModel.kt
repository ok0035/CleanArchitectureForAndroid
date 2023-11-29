package com.zerodeg.feature_video.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.arthenica.ffmpegkit.FFmpegKit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoEditorViewModel @Inject constructor() : ViewModel() {

    fun countVideoFrames(videoPath: String) {
        val cmd = "ffprobe -v error -select_streams v:0 -show_entries stream=nb_frames -of default=nokey=1:noprint_wrappers=1 $videoPath"
        Log.d("FFmpeg","FFmpegKit Cmd -> $cmd")

        FFmpegKit.executeAsync(cmd, { session ->
            val output = session.output
            val frameCount = output?.trim()?.toIntOrNull()

            if (frameCount != null) {
                // 프레임 수 처리
                Log.d("FFmpegKit", "Total frames: $frameCount")
            } else {
                // 오류 처리
                Log.e("FFmpegKit", "Failed to count frames")
            }
        }, { log ->
            // 로그 처리
            Log.d("FFmpegKit", log.message)
        }, { statistics ->
            // 통계 처리
        })
    }

}
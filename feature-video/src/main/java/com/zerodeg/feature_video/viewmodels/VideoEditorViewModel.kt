package com.zerodeg.feature_video.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFprobeKit
import com.zerodeg.domain.video_editor.VideoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VideoEditorViewModel @Inject constructor() : ViewModel() {

    val videoState: VideoState = VideoState(
        initialStart = 0.dp
    )

    private var isInitState = false

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        return try {
            // 임시 파일 생성
            val tempFile = File(context.cacheDir, "temp_video.mp4")
            tempFile.createNewFile()

            // Uri에서 InputStream을 얻고, 이를 임시 파일에 복사
            context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                FileOutputStream(tempFile).use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
            }

            // 임시 파일의 경로 반환
            tempFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun updateVideoTotalTime(videoPath: String) = viewModelScope.launch {
        val cmd =
            "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 $videoPath"

        Log.d("FFmpeg", "FFmpegKit Cmd -> $cmd")
        FFprobeKit.executeAsync(
            cmd, { session ->
                val output = session.output
                val frameTime = output?.trim()?.toDoubleOrNull()

                if (frameTime != null) {
                    Log.d("FFmpegKit", "success: $frameTime ${(frameTime * 1000).toInt()}")
                    videoState.totalTime = (frameTime * 1000).toInt()
                } else {
                    // 오류 처리
                    Log.e("FFmpegKit", "Failed")
                }
            },
            { log ->
                // 로그 처리
                Log.d("FFmpegKitLog", log.message)
            },

        )

    }

    fun initVideoState(start: Dp, end: Dp, width: Dp) {
        if(isInitState) return
        videoState.start = start
        videoState.end = end
        videoState.width = width
        isInitState = true
    }

    fun updateSelectedStartTime(newTime: Int) {
        videoState.selectedStartTime = newTime
    }

    fun updateSelectedEndTime(newTime: Int) {
        videoState.selectedEndTime = newTime
    }

}
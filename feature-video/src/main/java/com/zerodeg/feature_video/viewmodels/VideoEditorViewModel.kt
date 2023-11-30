package com.zerodeg.feature_video.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.zerodeg.domain.video_editor.VideoState
import com.zerodeg.feature_video.utils.VideoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VideoEditorViewModel @Inject constructor(
    private val videoUtils: VideoUtils
) : ViewModel() {

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

    fun updateVideoTotalTime(
        videoPath: String,
        onCompleteListener: ((totalTime: Long) -> Unit)? = null
    ) = viewModelScope.launch {
        val cmd =
            "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 $videoPath"

        Log.d("FFmpeg", "FFmpegKit Cmd -> $cmd")
        FFprobeKit.executeAsync(
            cmd,
            { session ->
                val output = session.output
                val frameTime = output?.trim()?.toDoubleOrNull()

                if (frameTime != null) {
                    Log.d("FFmpegKit", "success: $frameTime ${(frameTime * 1000).toInt()}")
                    videoState.totalTime = (frameTime * 1000).toInt()
                    onCompleteListener?.invoke(videoState.totalTime.toLong())
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
        if (isInitState) return
        videoState.start = start
        videoState.end = end
        videoState.width = width
        isInitState = true
    }

    fun updateSelectedStartTime(newTime: Int) {
        videoState.selectedStartTime = newTime
        videoState.selectedTime = newTime
        Log.d("UPDATE_PLAYER_START_TIME", "$newTime")
    }

    fun updateSelectedEndTime(newTime: Int) {
        videoState.selectedEndTime = newTime
        videoState.selectedTime = newTime
        Log.d("UPDATE_PLAYER_END_TIME", "$newTime")
    }

    private fun updateBitmapList(bitmaps: List<Bitmap>) {
        videoState.bitmapList = bitmaps
        Log.d("UPDATE_BITMAP_LIST", "${videoState.bitmapList}")
    }

    fun loadBitmap(retriever: MediaMetadataRetriever, time: Int) =
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val frameBitmap =
                    retriever.getFrameAtTime(time * 1000L)
                videoState.bitmap = frameBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }

        }

    //ms
    fun loadBitmaps(
        retriever: MediaMetadataRetriever,
        totalTime: Long,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmapList = mutableListOf<Bitmap>()
                for (i in 0..totalTime step 100) {
                    val frameBitmap = retriever.getFrameAtTime(i)
                    frameBitmap?.let {
                        bitmapList.add(frameBitmap)
                    }
                }
                updateBitmapList(bitmapList)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
                onComplete.invoke()
            }

        }

    fun encodeVideoWithKeyframeInterval(videoPath: String, outputPath: String, onCompletion: (uri: Uri) -> Unit) {
        val cmd = "-i $videoPath -g 1 -c:v h264_mediacodec -preset ultrafast -b:v 5000k $outputPath"
//        val cmd2 = "-i $videoPath -g 1 -c:v h264_mediacodec $outputPath"
        FFmpegKit.executeAsync(cmd) { session ->
            if (session.returnCode.isValueSuccess) {
                Log.d("FFmpegKit", "Encoding successful")
                val uri = Uri.parse(outputPath)
                onCompletion(uri)
            } else {
                Log.e("FFmpegKit", "Encoding failed")
            }
        }
    }

    fun getTempPath(newName: String) = videoUtils.getTempFilePath(newName)

}
package com.zerodeg.feature_video.viewmodels

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFprobeKit
import com.google.ads.interactivemedia.v3.internal.it
import com.zerodeg.domain.video_editor.VideoState
import com.zerodeg.feature_video.utils.VideoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoEditorViewModel @Inject constructor(
    private val videoUtils: VideoUtils
) : ViewModel() {

    val videoStateList: List<VideoState> = listOf(
        VideoState(), VideoState(), VideoState()
    )
    var isLoading by mutableStateOf<Boolean>(false)
    var loadingMsg: MutableState<String> = mutableStateOf("비디오를 불러오고 있어요.")
    val selectedVideoIdx = mutableIntStateOf(0)

    fun getMediaSource(uri: Uri) = videoUtils.getMediaSource(uri)

    fun updateVideoTotalTime(
        videoPath: String, onCompleteListener: ((totalTime: Long) -> Unit)? = null
    ) = viewModelScope.launch {
        isLoading = true
        loadingMsg.value = "비디오 정보를 가져오고 있어요 !"
        videoUtils.updateVideoTotalTime(videoPath) {
            onCompleteListener?.invoke(it)
            isLoading = false
        }
    }

    fun loadBitmap(retriever: MediaMetadataRetriever, time: Int) =
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val frameBitmap = retriever.getFrameAtTime(time * 1000L)
//                videoStateList.bitmap = frameBitmap
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
        onSuccess: (bitmapList: List<Bitmap>) -> Unit,
        onComplete: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val step = (totalTime / 10)
            val bitmapList = mutableListOf<Bitmap>()
            for (i in 0..totalTime step step) {
                val frameBitmap = retriever.getFrameAtTime(i)
                frameBitmap?.let {
                    bitmapList.add(frameBitmap)
                }
            }
            onSuccess(bitmapList)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
            onComplete.invoke()
        }

    }

    fun encodeVideoWithKeyframeInterval(
        videoPath: String, outputPath: String, onCompletion: (uri: Uri) -> Unit
    ) = viewModelScope.launch {
        //loading start
        isLoading = true
        loadingMsg.value = "비디오를 열심히 불러오고 있어요 !"
        videoUtils.encodeVideoWithKeyframeInterval(videoPath, outputPath) {
            //loading end
            onCompletion(it)
            isLoading = false
        }
    }

    fun trimVideo() = viewModelScope.launch {
        isLoading = true
        loadingMsg.value = "비디오를 잘라내고 있어요 ~ 싹둑싹둑 !"
        val videoState = videoStateList[selectedVideoIdx.intValue]
        videoUtils.trimVideo(
            inputPath = videoState.uri?.path,
            start = videoState.startTime.toDouble() / 1000.0,
            end = videoState.endTime.toDouble() / 1000.0,
            onError = {
                Log.d("trim", "error")
                isLoading = false
            },
            onSuccess = {path ->
                Log.d("trim", "success -> $path")
                loadingMsg.value = "비디오 정보를 가져오고 있어요 !"
                videoUtils.updateVideoTotalTime(path) {
                    Log.d("trim", "get length -> $it")
                    videoStateList[selectedVideoIdx.intValue].apply {
                        uri = Uri.parse(path)
                        start = -(1.dp)
                        end = -(1.dp)
                        width = -(1.dp)
                        totalTime = it.toInt()
                        startTime = 0
                        endTime = it.toInt()
                        selectedTime = 0
                    }
                }

                loadingMsg.value = ""
                isLoading = false

            }
        )
    }

    fun cutAndMergeVideos(
        outputFile: String,
        onError: () -> Unit,
        onSuccess: (uri: Uri) -> Unit
    ) = viewModelScope.launch {
//        val videoPaths = mutableListOf<VideoState>()
//        for (videoPathIdx in 0..2) {
//            when (videoPathIdx) {
//                0 -> {
//                    videoState1.uri?.path?.let {
//                        videoPaths.add(videoState1)
//                    }
//                }
//
//                1 -> {
//                    videoState2.uri?.path?.let {
//                        videoPaths.add(videoState2)
//                    }
//                }
//
//                2 -> {
//                    videoState3.uri?.path?.let {
//                        videoPaths.add(videoState3)
//                    }
//                }
//            }
//        }
        videoUtils.mergeVideos(
            videoPaths = videoStateList,
            outputFile = outputFile,
            onError = onError,
            onSuccess = {
                onSuccess(it)
            }
        )
    }

    fun deleteCacheData() = viewModelScope.launch { videoUtils.deleteTempFiles() }

    fun getLoadControl() = videoUtils.getLoadControl(
        0, 0, 0, 0, 0, true
    )

    fun getTempRandomPath(extension: String) = videoUtils.getTempFilePath(extension)
    fun getRealPathFromURI(contentUri: Uri): String? = videoUtils.getRealPathFromURI(contentUri)

}
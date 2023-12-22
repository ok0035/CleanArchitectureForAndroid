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
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.google.ads.interactivemedia.v3.internal.it
import com.zerodeg.domain.video_editor.VideoState
import com.zerodeg.feature_video.utils.VideoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val selectedVideoIdx = mutableIntStateOf(-1)

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
        uri: Uri,
        onSuccess: (bitmapList: List<Bitmap>) -> Unit,
        onComplete: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        isLoading = true
        loadingMsg.value = "비디오 이미지를 가져오고 있어요 !"
        videoUtils.loadBitmaps(
            uri,
            onSuccess = { bitmaps ->
                onSuccess(bitmaps)
            },
            onComplete = {
                viewModelScope.launch(Dispatchers.IO) {
                    onComplete.invoke()
                    delay(2000)
                    isLoading = false
                }

            }
        )

    }

    fun encodeVideoWithKeyframeInterval(
        videoPath: String, outputPath: String, onCompletion: (uri: Uri) -> Unit
    ) = viewModelScope.launch {
        //loading start
        isLoading = true
        val loadingCoroutine = viewModelScope.launch {
            while (isLoading) {
                loadingMsg.value = "동영상을 열심히 가져오고 있어요 !"
                delay(5000)
                if (!isLoading) break
                loadingMsg.value = "품질이 좋은 영상인 것 같아요 !"
                delay(5000)
                if (!isLoading) break
                loadingMsg.value = "거의 다 가져왔어요 !"
                delay(5000)
                if (!isLoading) break
            }
        }
        loadingCoroutine.start()
        videoUtils.encodeVideoWithKeyframeInterval(videoPath, outputPath) {
            //loading end
            onCompletion(it)
            loadingCoroutine.cancel()
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
            onSuccess = { path ->
                Log.d("trim", "success -> $path")
                loadingMsg.value = "비디오 정보를 가져오고 있어요 !"
                loadBitmaps(
                    Uri.parse(path),
                    onSuccess = { bitmaps ->

                        loadingMsg.value = "이제 거의 다 됐어요 !"
                        videoUtils.updateVideoTotalTime(path) {
                            Log.d("trim", "get length -> $it")
                            videoStateList[selectedVideoIdx.intValue].apply {
                                uri = Uri.parse(path)
                                start = 0.dp
                                end = -(1.dp)
                                width = -(1.dp)
                                totalTime = it
                                startTime = 0
                                endTime = it
                                selectedTime = 0
                                bitmapList = bitmaps
                            }
                        }

                    },
                    onComplete = {

                    }
                )

                isLoading = false

            }
        )
    }

    fun cutAndMergeVideos(
        outputFile: String,
        onError: () -> Unit,
        onSuccess: (uri: Uri) -> Unit
    ) = viewModelScope.launch {

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
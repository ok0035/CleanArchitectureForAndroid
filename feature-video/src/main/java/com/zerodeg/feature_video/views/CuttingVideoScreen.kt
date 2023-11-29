package com.zerodeg.feature_video.views

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel
import kotlin.math.abs

@Composable
fun VideoFrameSelector(viewModel: VideoEditorViewModel) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {

            val maxWidth = (constraints.maxWidth.dp.value / LocalDensity.current.density).dp
            viewModel.initVideoState(0.dp, maxWidth, maxWidth)

            Box(
                modifier = Modifier
                    .width(viewModel.videoState.width)
                    .offset(x = viewModel.videoState.start)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            ) {

                val dragBarWidth = 10.dp

                //Start Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Black)
                        .align(Alignment.CenterStart)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newStart = viewModel.videoState.start + dragAmount.x.toDp()
                                val endBar =
                                    (viewModel.videoState.end - (dragBarWidth.value * 2).dp)

                                if (newStart < 0.dp) newStart = 0.dp
                                else if (newStart >= endBar)
                                    newStart = endBar
                                viewModel.videoState.start = newStart
                                viewModel.videoState.width =
                                    viewModel.videoState.end - viewModel.videoState.start


                                val newTime = (viewModel.videoState.totalTime / maxWidth.value * viewModel.videoState.start.value).toInt()
                                        .coerceIn(0, viewModel.videoState.totalTime - 1)

                                if (abs(viewModel.videoState.selectedStartTime - newTime) > 0.25)
                                    viewModel.updateSelectedStartTime(newTime)

                                Log.d(
                                    "CHANGE",
                                    "start ${viewModel.videoState.start} end -> ${viewModel.videoState.end} ${viewModel.videoState.selectedStartTime}"
                                )

                            }
                        }
                )

                //End Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Black)
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newEnd = viewModel.videoState.end + dragAmount.x.toDp()
                                val startBar =
                                    (viewModel.videoState.start + (dragBarWidth.value * 2).dp)
                                val endOfView = (maxWidth)

                                if (newEnd <= startBar)
                                    newEnd = startBar
                                else if (newEnd >= endOfView) newEnd = endOfView

                                viewModel.videoState.end = newEnd
                                viewModel.videoState.width =
                                    viewModel.videoState.end - viewModel.videoState.start


                                val newTime = (viewModel.videoState.totalTime / maxWidth.value * viewModel.videoState.end.value).toInt()
                                    .coerceIn(0, viewModel.videoState.totalTime - 1)

                                if (abs(viewModel.videoState.selectedEndTime - newTime) > 0.25)
                                    viewModel.updateSelectedEndTime(newTime)

                                Log.d(
                                    "CHANGE",
                                    "start ${viewModel.videoState.start} end -> ${viewModel.videoState.end} ${viewModel.videoState.selectedEndTime}"
                                )
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun GalleryVideoPicker(onVideoPicked: (Uri?) -> Unit) {
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onVideoPicked(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Column을 부모 컨테이너 크기만큼 채움
            .padding(16.dp), // 패딩 추가
        verticalArrangement = Arrangement.Bottom, // 세로 방향으로 하단 정렬
        horizontalAlignment = Alignment.CenterHorizontally // 가로 방향으로 중앙 정렬
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Row를 부모 컨테이너 너비만큼 채움
            horizontalArrangement = Arrangement.Center // 가로 방향으로 중앙 정렬
        ) {
            Button(
                onClick = { videoPickerLauncher.launch("video/*") },
            ) {
                Text("Select Video")
            }
        }
    }

}

@Composable
fun VideoFrameImage(videoUri: Uri?, viewModel: VideoEditorViewModel) {
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    fun loadBitmap(videoUri: Uri?, time: Int) {
        videoUri?.let { uri ->
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val frameBitmap =
                    retriever.getFrameAtTime(time * 1000L)
                viewModel.videoState.bitmap = frameBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }

    LaunchedEffect(
        videoUri
    ) {
        loadBitmap(
            videoUri,
            1
        )
    }

    LaunchedEffect(
        viewModel.videoState.selectedStartTime
    ) {
        loadBitmap(
            videoUri,
            viewModel.videoState.selectedStartTime
        )
    }

    LaunchedEffect(
        viewModel.videoState.selectedEndTime
    ) {
        loadBitmap(
            videoUri,
            viewModel.videoState.selectedEndTime
        )
    }

    viewModel.videoState.bitmap?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(300.dp)
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(12.dp)) // 여기에 clip 적용
                    .background(Color.Black)
            ) {
                Image(
                    bitmap = viewModel.videoState.bitmap!!.asImageBitmap(),
                    contentDescription = "Video Frame",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }

}

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

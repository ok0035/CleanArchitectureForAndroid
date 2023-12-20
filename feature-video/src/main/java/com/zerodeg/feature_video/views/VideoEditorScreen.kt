package com.zerodeg.feature_video.views

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zerodeg.domain.video_editor.VideoState
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel
import kotlin.math.abs

@Composable
fun VideoFrameSelector(videoState: VideoState) {

    val dragSpace = 200
    var maxWidth by remember { mutableStateOf(0.dp) }

    LaunchedEffect(key1 = maxWidth) {
        videoState.apply {

            if (videoState.start == -(1.dp))
                start = 0.dp

            if(videoState.end == -(1.dp))
                end = maxWidth

            if(videoState.width == -(1.dp))
                width = maxWidth
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {


        BoxWithConstraints(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {

            maxWidth = (constraints.maxWidth.dp.value / LocalDensity.current.density).dp

            Box(
                modifier = Modifier
                    .width(videoState.width)
                    .offset(x = videoState.start)
                    .fillMaxHeight()
                    .background(Color.DarkGray),
            ) {

                val dragBarWidth = 10.dp

                //Start Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Yellow)
                        .align(Alignment.CenterStart)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newStart = videoState.start + dragAmount.x.toDp()
                                val endBar =
                                    (videoState.end - (dragBarWidth.value * 2).dp)

                                if (newStart < 0.dp) newStart = 0.dp
                                else if (newStart >= endBar)
                                    newStart = endBar
                                videoState.start = newStart
                                videoState.width =
                                    videoState.end - videoState.start

                                val newTime =
                                    (videoState.totalTime / maxWidth.value * videoState.start.value)
                                        .toInt()
                                        .coerceIn(0, videoState.totalTime - 1)

                                if (abs(videoState.startTime - newTime) > dragSpace) {
                                    videoState.startTime = newTime
                                    videoState.selectedTime = newTime
                                }

                                Log.d(
                                    "CHANGE",
                                    "start ${videoState.start} end -> ${videoState.end} ${videoState.startTime}"
                                )

                            }
                        }
                )

                //End Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Yellow)
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newEnd = videoState.end + dragAmount.x.toDp()
                                val startBar =
                                    (videoState.start + (dragBarWidth.value * 2).dp)
                                val endOfView = (maxWidth)

                                if (newEnd <= startBar)
                                    newEnd = startBar
                                else if (newEnd >= endOfView) newEnd = endOfView

                                videoState.end = newEnd
                                videoState.width =
                                    videoState.end - videoState.start

                                val newTime =
                                    (videoState.totalTime / maxWidth.value * videoState.end.value)
                                        .toInt()
                                        .coerceIn(0, videoState.totalTime - 1)

                                if (abs(videoState.endTime - newTime) > dragSpace) {
//                                    viewModel.updateSelectedEndTime(newTime)
                                    videoState.endTime = newTime
                                    videoState.selectedTime = newTime
                                }

                                Log.d(
                                    "CHANGE",
                                    "start ${videoState.start} end -> ${videoState.end} ${videoState.endTime}"
                                )
                            }
                        }
                )
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(state: VideoState) {

    val viewModel: VideoEditorViewModel = hiltViewModel()
    val context = LocalContext.current
    val playbackSpeed = 1.0f

    val exoPlayer = remember {
        ExoPlayer.Builder(context, DefaultRenderersFactory(context).setEnableDecoderFallback(true))
            .setLoadControl(viewModel.getLoadControl())
            .setSeekParameters(SeekParameters.NEXT_SYNC)
            .build()
            .apply {
                playWhenReady = false
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                playbackParameters = PlaybackParameters(playbackSpeed, 1.0f)
            }
    }

    val playerView = remember {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        }
    }

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Unspecified)
                .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            var size by remember { mutableStateOf(Size.Zero) }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)) // 여기에 clip 적용
                    .height(300.dp)
                    .wrapContentWidth()
                    .padding(1.dp)
                    .background(Color.Unspecified)
                    .onSizeChanged { newSize ->
                        size = newSize.toSize()
                        Log.d("size", "size -> $size")
                    },
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Yellow)
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    factory = {
                        playerView
                    })

                DraggableBox(
                    parentWidth = size.width, parentHeight = size.height
                )
            }
        }

        state.uri?.let {
            VideoFrameSelector(videoState = state)
        }

    }
    LaunchedEffect(state.selectedTime) {
        val selectedTime = state.selectedTime.toLong()
        playerView.player?.seekTo(selectedTime)
        Log.d("VIDEO", "SEEK TO $selectedTime")
    }


    LaunchedEffect(viewModel.videoStateList[viewModel.selectedVideoIdx.intValue].uri) {

        state.uri?.let {
            Log.d("VIDEO", "CHANGE INTO $it")
            exoPlayer.setMediaSource(viewModel.getMediaSource(it))
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
//            viewModel.deleteCacheData()
//            exoPlayer.release()
        }
    }
}

@Composable
fun GalleryVideoPicker(onVideoPicked: () -> Unit) {


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
                onClick = { onVideoPicked() },
            ) {
                Text("Select Video")
            }
        }
    }

}

@Composable
fun DraggableBox(parentWidth: Float, parentHeight: Float) {
    val offset = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = offset.value.x.toInt(),
                    y = offset.value.y.toInt()
                )
            }
            .wrapContentHeight()
            .wrapContentWidth()
            .background(Color.Unspecified)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    val newX =
                        (offset.value.x + dragAmount.x)
                    val newY =
                        (offset.value.y + dragAmount.y)
                    offset.value = Offset(newX, newY)
                    Log.d("pointerInput", "offset -> $dragAmount $parentWidth $parentHeight")
                }
            }
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            text = "은평한옥마을",
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight(400)
        )
    }
}

@Composable
fun GradientText() {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(40.dp)
    ) {
        val text = ""
        val fontSize = 30.sp
        val colors = listOf(Color.Cyan, Color.Blue, Color.Cyan)
//        val shaderBrush = ShaderBrush(LinearGradient(colors))

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = with(LocalDensity.current) { fontSize.toPx() }
            typeface = android.graphics.Typeface.MONOSPACE
        }

        val textWidth = paint.measureText(text)
        val shader = android.graphics.LinearGradient(
            0f, 0f, textWidth, 0f,
            colors.map { it.toArgb() }.toIntArray(),
            null,
            android.graphics.Shader.TileMode.CLAMP
        )

        paint.shader = shader

        Canvas(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center),
        ) {
            drawContext.canvas.nativeCanvas.drawText(
                text,
                0f,
                size.height / 2,
                paint
            )
        }
    }
}

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

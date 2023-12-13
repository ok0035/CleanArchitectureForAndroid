package com.zerodeg.feature_video.views

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel
import kotlin.math.abs

@Composable
fun VideoFrameSelector() {

    val viewModel: VideoEditorViewModel = hiltViewModel()
    val dragSpace = 200

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


                                val newTime =
                                    (viewModel.videoState.totalTime / maxWidth.value * viewModel.videoState.start.value)
                                        .toInt()
                                        .coerceIn(0, viewModel.videoState.totalTime - 1)

                                if (abs(viewModel.videoState.selectedStartTime - newTime) > dragSpace)
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

                                val newTime =
                                    (viewModel.videoState.totalTime / maxWidth.value * viewModel.videoState.end.value)
                                        .toInt()
                                        .coerceIn(0, viewModel.videoState.totalTime - 1)

                                if (abs(viewModel.videoState.selectedEndTime - newTime) > dragSpace)
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
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uri: Uri?) {

    if (uri == null) return

    val context = LocalContext.current
    val viewModel: VideoEditorViewModel = hiltViewModel()

    val exoPlayer = remember(uri) {

        Log.d("EXO_PLAYER", "init exo player")

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                0, // 최소 버퍼링 시간 (15초)
                0, // 최대 버퍼링 시간 (30초)
                0,  // 재생을 위한 버퍼링 시간 (2.5초)
                0   // 재버퍼링 후 재생을 위한 시간 (5초)
            )
            .setBackBuffer(0, true) // 백버퍼 길이 (10초), 재생 중에도 유지
            .build()

        val playbackSpeed = 1.0f

        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setSeekParameters(SeekParameters.NEXT_SYNC)
            .build()
            .apply {
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))

                playWhenReady = false
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                playbackParameters = PlaybackParameters(playbackSpeed, 1.0f)

                setMediaSource(source)
                prepare()

            }
    }

    val playerView = remember {
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
    }

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
            AndroidView(factory = {
                playerView
            })
        }
    }

    LaunchedEffect(viewModel.videoState.selectedTime) {
        val selectedTime = viewModel.videoState.selectedTime.toLong()
        playerView.player?.seekTo(selectedTime)
        Log.d("VIDEO", "SEEK TO $selectedTime")
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
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
fun VideoFrameImage(videoUri: Uri?) {
    val viewModel: VideoEditorViewModel = hiltViewModel()
    val context = LocalContext.current

    val retriever = MediaMetadataRetriever()
    videoUri?.let { uri ->
        retriever.setDataSource(context, uri)
    }

    LaunchedEffect(
        videoUri
    ) {
        viewModel.loadBitmap(
            retriever,
            1
        )
    }

    LaunchedEffect(
        viewModel.videoState.selectedStartTime
    ) {
        viewModel.loadBitmap(
            retriever,
            viewModel.videoState.selectedStartTime
        )
    }

    LaunchedEffect(
        viewModel.videoState.selectedEndTime
    ) {
        viewModel.loadBitmap(
            retriever,
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
fun GradientText() {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(40.dp)
    ) {
        val text = "안녕하세요"
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

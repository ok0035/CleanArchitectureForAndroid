package com.zerodeg.feature_video.views

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.zerodeg.domain.video_editor.VideoState
import com.zerodeg.feature_video.utils.VideoFilterState
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun VideoFrameSelector(modifier: Modifier, videoState: VideoState) {

    val viewModel: VideoEditorViewModel = hiltViewModel()

    val dragSpace = 200
    var maxWidth by remember { mutableStateOf(0.dp) }
    val dragBarWidth = 10.dp

    LaunchedEffect(key1 = maxWidth, key2 = videoState.totalTime) {
        videoState.apply {

            if (videoState.start == -(1.dp)) {
                start = 0.dp
                startTime = 0
            }

            if (videoState.end == -(1.dp)) {
                end = maxWidth
                endTime = totalTime
            }

            if (videoState.width == -(1.dp))
                width = maxWidth
        }
    }

    Box(
        modifier = modifier
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Unspecified)
                .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
        ) {

            maxWidth = (constraints.maxWidth.dp.value / LocalDensity.current.density).dp

            Row(Modifier.fillMaxSize()) {
                val imageWidth = maxWidth / (videoState.bitmapList?.size ?: 1)
                val bitmapList = videoState.bitmapList

                bitmapList?.let { bitmaps ->
                    for (bitmap in bitmaps) {
                        Image(
                            modifier = Modifier
                                .width(imageWidth)
                                .fillMaxHeight(),
                            bitmap = bitmap.asImageBitmap(),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = null
                        )
                    }
                }

            }

            //Playing Bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(1.dp, 0.dp)
                    .offset(x = ((videoState.playingTime.toDouble() * maxWidth.value.toDouble()) / videoState.totalTime.toDouble()).dp)
                    .background(Color.Black)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

//                                videoState.selectedTime =
//                                    (videoState.totalTime / maxWidth.value * videoState.start.value).toInt()
                            val playingPos =
                                ((videoState.playingTime * maxWidth.value) / videoState.totalTime).dp

                            videoState.width =
                                videoState.end - videoState.start

                            Log.d("PlayingBar", "PlayingPos -> $playingPos")

                            var newPlayingPos = playingPos + dragAmount.x.toDp()
                            val endBar = (videoState.end)

                            if (newPlayingPos < videoState.start) newPlayingPos =
                                videoState.start
                            else if (newPlayingPos >= endBar)
                                newPlayingPos = endBar

                            val newTime =
                                ((videoState.totalTime / maxWidth.value) * newPlayingPos.value)
                                    .toLong()
                                    .coerceIn(0, videoState.totalTime - 1)

                            videoState.playingTime = newTime
                            videoState.selectedTime = newTime

                            Log.d(
                                "CHANGE",
                                "Player Bar -> total time : ${videoState.totalTime} new time : $newTime newPlayingPos : ${newPlayingPos.value} dragAmount : ${dragAmount.x.dp}"
                            )

                        }
                    }
            )

            Box(
                modifier = Modifier
                    .width(videoState.width)
                    .offset(x = videoState.start)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.4f))
            ) {

                //Start Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color(0XFF0FD3D8))
                        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
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
                                        .toLong()
                                        .coerceIn(0, videoState.totalTime - 1)

//                                if (abs(videoState.startTime - newTime) > dragSpace) {
                                videoState.startTime = newTime
                                videoState.selectedTime = newTime
//                                }

                                Log.d(
                                    "CHANGE",
                                    "start ${videoState.start} end -> ${videoState.end} startTime  ${videoState.startTime} totalTime ${videoState.totalTime} width $maxWidth"
                                )

                            }
                        }
                )

                //End Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color(0XFF0FD3D8))
                        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
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
                                        .toLong()
                                        .coerceIn(0, videoState.totalTime - 1)

                                videoState.endTime = newTime
                                videoState.selectedTime = newTime

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
fun VideoEditor(state: VideoState, navController: NavController) {

    val viewModel: VideoEditorViewModel = hiltViewModel()
    val context = LocalContext.current
    val playbackSpeed = 1.0f

    val playingCoroutineJob = remember { mutableStateOf<Job?>(null) }

    // 이벤트에 따라 코루틴 시작
    val startPlay = remember { mutableStateOf(false) }

    if (state.uri == null) return

    val exoPlayer = remember {
        ExoPlayer.Builder(context, DefaultRenderersFactory(context).setEnableDecoderFallback(true))
            .setLoadControl(viewModel.getLoadControl())
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .setSeekBackIncrementMs(10)
            .setSeekForwardIncrementMs(10)
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
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    state.uri?.let {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Unspecified)
        ) {

            var size by remember { mutableStateOf(Size.Zero) }
            val player = createRef()

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)) // 여기에 clip 적용
                    .constrainAs(player) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 40.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .background(Color.Unspecified)
                    .onSizeChanged { newSize ->
                        size = newSize.toSize()
                        Log.d("size", "size -> $size")
                    },
            ) {

                Column(
                    modifier = Modifier.wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AndroidView(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .padding(top = 20.dp, bottom = 20.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Unspecified)
                            .wrapContentWidth(),
                        factory = {
                            playerView
                        })

                    Box(contentAlignment = Alignment.Center) {

                        Box(
                            Modifier
                                .background(Color.White)
                                .width(45.dp)
                                .height(45.dp)
                        )
                        Image(
                            painter = painterResource(id = com.tenfingers.core_res.R.drawable.icon_play_pause_48),
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                                .clickable {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        startPlay.value = false
                                    } else if (!exoPlayer.isPlaying) {
                                        exoPlayer.play()
                                        startPlay.value = true
                                    }
                                },
                            contentDescription = null
                        )
                    }

                    VideoFrameSelector(
                        modifier = Modifier
                            .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                            .fillMaxWidth()
                            .height(100.dp),
                        videoState = state
                    )

                    VideoFilter(
                        modifier = Modifier
                            .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )

                }

//            DraggableBox(
//                parentWidth = size.width, parentHeight = size.height
//            )
            }

        }
    }

    LaunchedEffect(state.selectedTime) {
        val selectedTime = state.selectedTime.toLong()
        playerView.player?.seekTo(selectedTime)
        Log.d("VIDEO", "SEEK TO $selectedTime")
    }


    LaunchedEffect(
        key1 = viewModel.videoStateList[viewModel.selectedVideoIdx.intValue].uri,
    ) {

        state.uri?.let {
            Log.d("VIDEO", "CHANGE INTO $it")
            if (exoPlayer.isPlaying) exoPlayer.pause()
            exoPlayer.setMediaSource(viewModel.getMediaSource(it))
            exoPlayer.prepare()
        }

    }


    LaunchedEffect(
        key1 = viewModel.videoStateList[viewModel.selectedVideoIdx.intValue].filteredUri
    ) {

        val uri = state.filteredUri ?: state.uri ?: return@LaunchedEffect
        Log.d("VIDEO", "CHANGE FILTER INTO $uri")
        exoPlayer.setMediaSource(viewModel.getMediaSource(uri))
        exoPlayer.prepare()
    }

    LaunchedEffect(startPlay.value) {
        if (startPlay.value) {
            // 코루틴 시작
            playingCoroutineJob.value = launch {
                while (isActive) {
                    if (state.playingTime >= state.endTime) {
                        Log.d("Playing", "1111 start : ${state.startTime} end : ${state.endTime}")
                        state.playingTime = state.startTime
                        exoPlayer.seekTo(state.startTime)
                    } else {
                        Log.d(
                            "Playing",
                            "2222 start : ${state.startTime} end : ${state.endTime} ${exoPlayer.currentPosition}"
                        )
                        state.playingTime = exoPlayer.currentPosition
                    }
                    delay(10) // 일정 간격마다 반복
                }
            }
        } else {
            playingCoroutineJob.value?.cancel()
        }
    }

}

@Composable
fun DraggableBox(parentWidth: Float, parentHeight: Float) {

    if (parentWidth == 0.0f || parentHeight == 0.0f) return

    Log.d("DraggableBox", "$parentWidth $parentHeight")

    val offset = remember {
        mutableStateOf(
            Offset(
                x = (parentHeight / 2),
                y = (parentWidth / 2)
            )
        )
    }

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
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight(600)
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
fun SelectVideoView(
    modifier: Modifier,
    videoImage: ImageBitmap? = null,
    isSelect: Boolean = false,
    onClick: () -> Unit
) {

    val newModifier =
        if (isSelect)
            modifier
                .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f))
                .clickable {
                    onClick()
                }
        else {
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f))
                .clickable {
                    onClick()
                }
        }

    Box(
        modifier = newModifier,
        contentAlignment = Alignment.Center
    ) {


        if (videoImage != null)
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.FillHeight,
                bitmap = videoImage,
                contentDescription = null
            )
        else {

            Text(
                text = "SELECT\nVIDEO",
                color = Color.Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(600)
            )

        }

    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(state: VideoState, navController: NavController) {

    val viewModel: VideoEditorViewModel = hiltViewModel()
    val context = LocalContext.current
    val playbackSpeed = 1.0f

    val playingCoroutineJob = remember { mutableStateOf<Job?>(null) }

    // 이벤트에 따라 코루틴 시작
    val startPlay = remember { mutableStateOf(false) }

    if (state.uri == null) return

    val exoPlayer = remember {
        ExoPlayer.Builder(context, DefaultRenderersFactory(context).setEnableDecoderFallback(true))
            .setLoadControl(viewModel.getLoadControl())
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .setSeekBackIncrementMs(10)
            .setSeekForwardIncrementMs(10)
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
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }

    state.uri?.let {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Unspecified)
        ) {

            var size by remember { mutableStateOf(Size.Zero) }
            val player = createRef()

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)) // 여기에 clip 적용
                    .constrainAs(player) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 40.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .background(Color.Unspecified)
                    .onSizeChanged { newSize ->
                        size = newSize.toSize()
                        Log.d("size", "size -> $size")
                    },
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AndroidView(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .padding(top = 20.dp, bottom = 20.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Unspecified)
                            .wrapContentWidth(),
                        factory = {
                            playerView
                        })

                    Box(contentAlignment = Alignment.Center) {

                        Box(
                            Modifier
                                .background(Color.White)
                                .width(45.dp)
                                .height(45.dp)
                        )
                        Image(
                            painter = painterResource(id = com.tenfingers.core_res.R.drawable.icon_play_pause_48),
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                                .clickable {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        startPlay.value = false
                                    } else if (!exoPlayer.isPlaying) {
                                        exoPlayer.play()
                                        startPlay.value = true
                                    }
                                },
                            contentDescription = null
                        )
                    }

                    VideoFrameSelector(
                        modifier = Modifier
                            .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                            .fillMaxWidth()
                            .height(100.dp),
                        videoState = state
                    )

                }

            }

        }
    }

    LaunchedEffect(state.selectedTime) {
        val selectedTime = state.selectedTime.toLong()
        playerView.player?.seekTo(selectedTime)
        Log.d("VIDEO", "SEEK TO $selectedTime")
    }


    LaunchedEffect(
        key1 = viewModel.videoStateList[viewModel.selectedVideoIdx.intValue].uri,
    ) {

        state.uri?.let {
            Log.d("VIDEO", "CHANGE INTO $it")
            exoPlayer.setMediaSource(viewModel.getMediaSource(it))
            exoPlayer.prepare()
        }

    }


    LaunchedEffect(
        key1 = viewModel.videoStateList[viewModel.selectedVideoIdx.intValue].filteredUri
    ) {

        state.filteredUri?.let {
            Log.d("VIDEO", "CHANGE FILTER INTO $it")
            exoPlayer.setMediaSource(viewModel.getMediaSource(it))
            exoPlayer.prepare()
        }

    }

    LaunchedEffect(startPlay.value) {
        if (startPlay.value) {
            // 코루틴 시작
            playingCoroutineJob.value = launch {
                while (isActive) {
                    if (state.playingTime >= state.endTime) {
                        Log.d("Playing", "1111 start : ${state.startTime} end : ${state.endTime}")
                        state.playingTime = state.startTime
                        exoPlayer.seekTo(state.startTime)
                    } else {
                        Log.d(
                            "Playing",
                            "start : ${state.startTime} end : ${state.endTime} ${exoPlayer.currentPosition}"
                        )
                        state.playingTime = exoPlayer.currentPosition
                    }
                    delay(10) // 일정 간격마다 반복
                }
            }
        } else {
            playingCoroutineJob.value?.cancel()
        }
    }

}

@Composable
fun VideoFilter(
    modifier: Modifier
) {
    val viewModel: VideoEditorViewModel = hiltViewModel()

    LazyRow(
        modifier = modifier
    ) {

        items(viewModel.filterStateList) {
            when(it) {
                VideoFilterState.RESET -> {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable {
                                Log.d("VideoEditorViewModel", "onClick gray filter2")
                                viewModel.resetFilter()
                            },
                        text = "RESET",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(600)
                    )

                }

                VideoFilterState.GRAY_SCALE -> {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable {
                                Log.d("VideoEditorViewModel", "onClick gray filter2")
                                viewModel.filter(
                                    VideoFilterState.GRAY_SCALE,
                                    onSuccess = {

                                    }, onError = {

                                    }
                                )
                            },
                        text = "GRAY",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(600)
                    )
                }

                VideoFilterState.EDGE_BLUR -> {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable {
                                Log.d("VideoEditorViewModel", "onClick gray filter2")
                                viewModel.filter(
                                    VideoFilterState.EDGE_BLUR,
                                    onSuccess = {

                                    }, onError = {

                                    }
                                )
                            },
                        text = "BLUR",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(600)
                    )
                }

                VideoFilterState.BRIGHTNESS_CONTRAST -> {
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable {
                                Log.d("VideoEditorViewModel", "onClick gray filter2")
                                viewModel.filter(
                                    VideoFilterState.BRIGHTNESS_CONTRAST,
                                    onSuccess = {

                                    }, onError = {

                                    }
                                )
                            },
                        text = "CONTRAST",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(600)
                    )
                }
            }
        }

    }

}

@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

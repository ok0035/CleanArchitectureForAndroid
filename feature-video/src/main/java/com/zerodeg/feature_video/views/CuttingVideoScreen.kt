package com.zerodeg.feature_video.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerodeg.domain.video_editor.BoxState

@Composable
fun VideoFrameSelector(totalFrames: Int, onFrameSelected: (Int) -> Unit) {

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

            val width = (constraints.maxWidth.dp.value / LocalDensity.current.density).dp
            val boxState = remember { BoxState(0.dp, width, width) } // 초기 start, end 값

            val frameWidth = width / totalFrames
            var selectedFrame by remember { mutableIntStateOf(0) }

            Log.d("CuttingVideo", "width -> $width")

            Box(
                modifier = Modifier
                    .width(boxState.width)
                    .offset(x = boxState.start)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            ) {

                val dragBarWidth = 10.dp

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Black)
                        .align(Alignment.CenterStart)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newStart = boxState.start + dragAmount.x.toDp()
                                val endBar = (boxState.end - (dragBarWidth.value * 2).dp)

                                if (newStart < 0.dp) newStart = 0.dp
                                else if (newStart >= endBar)
                                    newStart = endBar
                                boxState.start = newStart
                                boxState.width = boxState.end - boxState.start
                                Log.d("CHANGE", "start -> ${boxState.start} end -> ${boxState.end}")
                            }
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Black)
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newEnd = boxState.end + dragAmount.x.toDp()
                                val startBar = (boxState.start + (dragBarWidth.value * 2).dp)
                                val endOfView = (width)

                                if (newEnd <= startBar)
                                    newEnd = startBar
                                else if (newEnd >= endOfView) newEnd = endOfView

                                boxState.end = newEnd
                                boxState.width = boxState.end - boxState.start
                                Log.d(
                                    "CHANGE",
                                    "start ${boxState.start} end -> ${boxState.end} $endOfView"
                                )
                            }
                        }
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

@Preview
@Composable
fun VideoCuttingBar(totalFrames: Int, onFrameSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), // Row를 부모 컨테이너 너비만큼 채움
        horizontalArrangement = Arrangement.Center // 가로 방향으로 중앙 정렬
    ) {
        VideoFrameSelector(totalFrames, onFrameSelected)
    }
}
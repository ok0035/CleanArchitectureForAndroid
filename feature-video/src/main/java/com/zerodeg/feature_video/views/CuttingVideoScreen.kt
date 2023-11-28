package com.zerodeg.feature_video.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zerodeg.domain.video_editor.BoxState

@Composable
fun AdjustableWidthBox() {

    BoxWithConstraints {

        val maxWidth = constraints.maxWidth.dp // 최대 가로 길이
        val boxState = remember { BoxState(50.dp, 200.dp, maxWidth) } // 초기 start, end 값

        BoxWithConstraints(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                .width(maxWidth)
                .height(150.dp)
                .background(Color.LightGray)
        ) {

            val endPos = (constraints.maxWidth.dp.value / LocalDensity.current.density).dp

            Box(
                modifier = Modifier
                    .width((boxState.end - boxState.start))
                    .offset(x = boxState.start)
                    .fillMaxHeight()
                    .background(Color.Yellow)
            ) {

                val dragBarWidth = 20.dp

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dragBarWidth, 0.dp)
                        .background(Color.Blue)
                        .align(Alignment.CenterStart)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newStart = boxState.start + dragAmount.x.toDp()
                                val endBar = (boxState.end.value - dragBarWidth.value * 2).dp

                                if (newStart < 0.dp) newStart = 0.dp
                                else if (newStart >= endBar)
                                    newStart = endBar
                                boxState.start = newStart
                                Log.d("CHANGE", "start -> ${boxState.start}")
                            }
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(20.dp, 0.dp)
                        .background(Color.Red)
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newEnd = boxState.end + dragAmount.x.toDp()
                                val startBar = (boxState.start + (dragBarWidth.value * 2).dp)
                                val endOfView = (endPos)

                                if (newEnd <= startBar)
                                    newEnd = startBar
                                else if (newEnd >= endOfView) newEnd = endOfView

                                boxState.end = newEnd
                                Log.d("CHANGE", "start ${boxState.start} end -> ${boxState.end} $endOfView")
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
fun MainScreen() {
    AdjustableWidthBox()
}
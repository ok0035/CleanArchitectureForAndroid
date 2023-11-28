package com.zerodeg.feature_video.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerodeg.domain.video_editor.BoxState

@Composable
fun AdjustableWidthBox() {

    BoxWithConstraints {

        val maxWidth = constraints.maxWidth // 최대 가로 길이
        val boxState = remember { BoxState(50f, 200f, maxWidth) } // 초기 start, end 값

        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .width(maxWidth.dp)
                .height(150.dp)
                .background(Color.LightGray)
        ) {

            Box(
                modifier = Modifier
                    .width((boxState.end - boxState.start).dp)
                    .offset(x = boxState.start.dp)
                    .fillMaxHeight()
                    .background(Color.Yellow)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(20.dp, 0.dp)
                        .background(Color.Blue)
                        .align(Alignment.CenterStart)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                var newStart = boxState.start + dragAmount.x
                                if (newStart < 0) newStart = 0f
                                else if(newStart >= (boxState.end.dp.value - 20.dp.value))
                                    newStart = (boxState.end.dp.value - 20.dp.value)
                                boxState.start = newStart
                                Log.d("CHANGE", "${boxState.start} $density")
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
                                var newEnd = boxState.end + dragAmount.x
                                val startBar = (boxState.start + 20.dp.value)

                                if (newEnd <= startBar)
                                    newEnd = startBar

                                boxState.end = newEnd
                                Log.d("CHANGE", "${boxState.start} $density")
                            }
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreen() {
    AdjustableWidthBox()
}
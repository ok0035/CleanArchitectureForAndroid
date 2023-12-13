package com.zerodeg.feature_main.ui.image

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerodeg.feature_main.R
import kotlin.math.roundToInt

@Composable
fun RotatingImage(
    imagePainter: Painter,
    imagePainterFixed: Painter,
    offsetY: Float = 0f,
    scaleX: Float = 1.0f,
    scaleY: Float = 1.0f,
    alpha: Float = 1.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        Modifier
            .offset {
                IntOffset(x = 0, y = offsetY.roundToInt())
            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = imagePainterFixed,
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scaleX,
                    scaleY = scaleY,
                    alpha = alpha,
                ),
            alpha = alpha

        )

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .graphicsLayer(
                    rotationZ = angle,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    alpha = alpha,
                    transformOrigin = TransformOrigin.Center
                ),
            alpha = alpha

        )

    }
}

@Preview
@Composable
fun DraggableImageExample() {
    // 드래그 양을 추적하는 상태
    val dragAmount = remember { mutableFloatStateOf(0f) }
    val dragTrigger = 2.dp.value
    // 이미지의 위치, 크기 및 투명도에 대한 애니메이션 상태
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (dragAmount.floatValue < dragTrigger) -(1000.dp).value else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic), label = ""
    )

    val animatedOffsetY2 by animateFloatAsState(
        targetValue = if (dragAmount.floatValue < dragTrigger) -(700.dp).value else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOutCubic), label = ""
    )

    val animatedOffsetY3 by animateFloatAsState(
        targetValue = if (dragAmount.floatValue < dragTrigger) -(700.dp).value else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseInOutCubic), label = ""
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (dragAmount.floatValue > dragTrigger) 1f else 0.5f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic), label = ""
    )

    val animatedScal2 by animateFloatAsState(
        targetValue = if (dragAmount.floatValue > dragTrigger) 1f else 0.5f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic), label = ""
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (dragAmount.floatValue < dragTrigger) 0.5f else 1f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic), label = ""
    )

    val animatedAlpha2 by animateFloatAsState(
        targetValue = if (dragAmount.floatValue < dragTrigger) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic), label = ""
    )

    val deltaY = remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    deltaY.floatValue = delta
                },
                onDragStopped = { velocity ->
                    // 손가락을 떼었을 때 델타값 처리
                    // 예를 들어, 드래그 양을 리셋하거나 최종 위치 결정 등
                    android.util.Log.d(
                        "onDragStopped",
                        "velocity -> $velocity delta -> ${deltaY.floatValue}"
                    )
                    dragAmount.floatValue = deltaY.floatValue
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Column {

            RotatingImage(
                painterResource(id = R.drawable.frame),
                painterResource(id = R.drawable.potato),
                offsetY = animatedOffsetY,
                scaleX = animatedScale,
                scaleY = animatedScale,
                alpha = animatedAlpha
            )

            Box(
                modifier = Modifier
                    .padding(start = 56.dp, end = 56.dp, top = 12.dp)
                    .offset {
                        IntOffset(x = 0, y = animatedOffsetY2.roundToInt())
                    }
                    .graphicsLayer {
                        this.scaleX = animatedScale
                        this.scaleY = animatedScale
                    }
                    .alpha(animatedAlpha2),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "그라운드시소 서촌",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight(800)

                )

            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .padding(start = 56.dp, end = 56.dp, top = 12.dp)
                    .offset {
                        IntOffset(x = 0, y = animatedOffsetY3.roundToInt())
                    }
                    .graphicsLayer {
                        this.scaleX = animatedScale
                        this.scaleY = animatedScale
                    }
                    .alpha(animatedAlpha2),
                contentAlignment = Alignment.Center
            ) {


                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "은평한옥마을의 한옥들은 우리가 익히 잘 아는 북촌의 그것과는 다른 인상을 심어준다.",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400)
                )

            }

        }
    }
}
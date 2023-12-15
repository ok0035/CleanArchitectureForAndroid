package com.zerodeg.feature_main.ui.image

import android.graphics.BlurMaskFilter
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ads.interactivemedia.v3.internal.it
import com.zerodeg.feature_main.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
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

    val offsetRepeat by infiniteTransition.animateFloat(
        initialValue = 0.dp.value,
        targetValue = -30.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOutCubic, delayMillis = 3000),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0, StartOffsetType.Delay),

            ), label = ""
    )

    val alphaRepeat by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOutCubic, delayMillis = 3000),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0, StartOffsetType.Delay),

            ), label = ""
    )

    Box(
        Modifier
            .offset {
                IntOffset(x = 0, y = offsetY.roundToInt())
            }
            .graphicsLayer(
                alpha = alphaRepeat,
            ),
        contentAlignment = Alignment.Center
    ) {

//        BlurredCircle(scaleX, alpha)

        Image(
            painter = imagePainterFixed,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(x = 0, y = offsetRepeat.roundToInt())
                }
                .graphicsLayer(
                    scaleX = scaleX,
                    scaleY = scaleY,
                    alpha = alpha,
                ),

            )

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .offset {
                    IntOffset(x = 0, y = offsetRepeat.roundToInt())
                }
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

@Composable
fun BlurredCircle(scale: Float, alpha: Float) {
    val radius = with(LocalDensity.current) { 200.dp.toPx() }
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        isAntiAlias = true
        maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha,
            ),
    ) {
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(size.width / 2, size.height / 2, radius, paint)
        }
    }
}

@Preview
@Composable
fun DraggableImageExample() {

    val context = LocalContext.current
    val deviceHeight = context.resources.displayMetrics.heightPixels

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
        animationSpec = tween(durationMillis = 850, easing = EaseInOutCubic), label = ""
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (dragAmount.floatValue > dragTrigger) 1f else 0.5f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic), label = ""
    )

    val animatedScale2 by animateFloatAsState(
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

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .padding(start = 56.dp, end = 56.dp)
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RotatingImage(
            painterResource(id = R.drawable.frame),
            painterResource(id = R.drawable.potato),
            offsetY = deviceHeight - 200.dp.value,
            scaleX = animatedScale,
            scaleY = animatedScale,
            alpha = animatedAlpha
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BubbleViewPager() {
// Creates a 1-pager/viewport vertical pager with single page snapping
    val state = rememberPagerState { 10 }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) { 20.dp.value.toDp() } // 드래그해야 하는 최소 비율

    val scale = animateFloatAsState(
        targetValue = 1f - abs(state.currentPageOffsetFraction), label = ""
//        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic)
    )

    var dragDirection by remember { mutableStateOf("") }
    var dragAmountDp by remember { mutableStateOf(0.dp) }

    val otherPageScale = animateFloatAsState(
        targetValue = 0.5f, label = "",
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    dragDirection = if (dragAmount.y < 0) "위로 드래그" else "아래로 드래그"
                    dragAmountDp = with(density) { dragAmount.y.dp.value.toDp() }
                    Log.d("drag", "dragAmount -> ${dragAmount.y} $dragAmountDp")
                }
            },
        contentAlignment = Alignment.Center
    ) {

        VerticalPager(
            state = state,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { page ->

            val currentPageScale =
                if (state.currentPage == page) scale.value else otherPageScale.value

            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                RotatingImage(
                    painterResource(id = R.drawable.frame),
                    painterResource(id = R.drawable.potato),
                    offsetY = 0f,
                    scaleX = currentPageScale,
                    scaleY = currentPageScale,
                    alpha = 1f
                )
            }
        }
    }

    LaunchedEffect(dragAmountDp) {

        val targetPage = if (dragAmountDp < -threshold) {
            Log.d("drag", "$dragAmountDp $threshold")
            state.currentPage + 1

        } else if (dragAmountDp > threshold) {
            Log.d("drag2", "$dragAmountDp $threshold")
            state.currentPage - 1
        } else state.currentPage

        scope.launch {
            delay(200)
            if (state.currentPage != targetPage)
                state.animateScrollToPage(targetPage)
        }
    }

}

@Composable
fun CustomViewPager(
    pageCount: Int,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val density = LocalDensity.current

    val screenHeight = context.resources.displayMetrics.heightPixels
    var itemHeight by remember { mutableStateOf(0.dp) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var dragStartedAt by remember { mutableFloatStateOf(0f) }
    var currentPage by remember { mutableIntStateOf(0) }

    var isScrolling by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val threshold = with(density) { 20.toDp() } // 드래그해야 하는 최소 비율

//    val scale = animateFloatAsState(
//        targetValue = 1f - abs(state.currentPageOffsetFraction), label = ""
////        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic)
//    )

    var dragDirection by remember { mutableStateOf("") }
    var dragAmountValue by remember { mutableFloatStateOf(0f) }

    val otherPageScale = animateFloatAsState(
        targetValue = 0.5f, label = "",
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic)
    )

    LazyColumn(
        state = scrollState,
        userScrollEnabled = false,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if(isScrolling) return@detectDragGestures
                        isScrolling = true
                        val scrollOffset = screenHeight * (currentPage + if (dragAmountValue > 0) -1 else 1)
                        val targetPage = (scrollOffset / screenHeight).coerceIn(0, pageCount - 1)
                        val scrollDistance = (targetPage - currentPage) * screenHeight
                        currentPage = targetPage

                        coroutineScope.launch {
                            scrollState.animateScrollBy(
                                scrollDistance.toFloat(),
                                animationSpec = tween(
                                    durationMillis = 600,
                                    easing = CubicBezierEasing(0.85f, 0f, 0.15f, 1f) // EaseInOutCubic 유사
                                )
                            )
//                            currentPage = targetPage
                            dragAmountValue = 0f
//                            delay(600)
                            isScrolling = false
                        }
                    },
                    onDragStart = {},
                    onDragCancel = {},
                    onDrag = { change, dragAmount ->
                        dragAmountValue = dragAmount.y
                    }

                )
            }
    ) {
        items(pageCount) { pageIndex ->
            // 페이지 컨텐츠...
            Log.d("page", "pageIndex -> $pageIndex")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) {screenHeight.toDp()})
                    .onGloballyPositioned {
                        itemHeight = with(density) { it.size.height.toDp()}
                    }
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                RotatingImage(
                    painterResource(id = R.drawable.frame),
                    painterResource(id = R.drawable.potato),
                    offsetY = 0f,
                    scaleX = 1f,
                    scaleY = 1f,
                    alpha = 1f
                )
            }
        }
    }

//    LaunchedEffect(dragAmountDp) {
//
//        if (abs(dragAmountDp.value) < threshold.value || isScrolling) return@LaunchedEffect
//
//        scope.launch {
////            Log.d("drag3", "$dragAmountDp $threshold")
//            isScrolling = true
//            delay(200)
//
//            val targetPage =
//                if (dragAmountDp < -threshold) {
//                    currentPage + 1
//                    Log.d("drag", "$dragAmountDp $threshold")
//                } else if (dragAmountDp > threshold) {
//                    currentPage - 1
//                    Log.d("drag2", "$dragAmountDp $threshold")
//                } else {
//                    Log.d("drag3", "$dragAmountDp $threshold")
//                    -1
//                }
//
//            if (targetPage >= 0) {
//                val targetScrollOffset = if(targetPage > currentPage) (itemHeight).value else (-itemHeight.value)
//                Log.d("target", "scroll offset -> $targetScrollOffset")
//                coroutineScope.launch {
//                    scrollState.animateScrollBy(
//                        targetScrollOffset,
//                        animationSpec = tween(
//                            durationMillis = 600,
//                            easing = CubicBezierEasing(0.85f, 0f, 0.15f, 1f) // EaseInOutCubic 유사
//                        )
//                    )
//                    currentPage = targetPage
//                }
//            }
//            delay(600)
//            isScrolling = false
//        }
//    }
}

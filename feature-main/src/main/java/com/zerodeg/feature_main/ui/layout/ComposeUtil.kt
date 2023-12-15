//package com.zerodeg.feature_main.ui.layout
//
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.Card
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.text.style.TextForegroundStyle.Unspecified.alpha
//import androidx.compose.ui.unit.dp
//import com.google.android.material.math.MathUtils.lerp
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun ImageSliderWithAnimation(images: List<Painter>) {
//    val pagerState = rememberPagerState(
//
//    )
//
//    HorizontalPager(count = 4) { page ->
//        Card(
//            Modifier
//                .graphicsLayer {
//                    // Calculate the absolute offset for the current page from the
//                    // scroll position. We use the absolute value which allows us to mirror
//                    // any effects for both directions
//                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
//
//                    // We animate the scaleX + scaleY, between 85% and 100%
//                    lerp(
//                        start = 0.85f,
//                        stop = 1f,
//                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                    ).also { scale ->
//                        scaleX = scale
//                        scaleY = scale
//                    }
//
//                    // We animate the alpha, between 50% and 100%
//                    alpha = lerp(
//                        start = 0.5f,
//                        stop = 1f,
//                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                    )
//                }
//        ) {
//            // Card content
//        }
//    }
//
//    // 페이지 전환 애니메이션 추가 (EaseInOutCubic 유사)
//    LaunchedEffect(pagerState) {
//        pagerState.animateScrollToPage(
//            page = pagerState.currentPage,
//            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
//        )
//    }
//}
//
//// 사용 예시
//@Composable
//fun PreviewImageSliderWithAnimation() {
//    val images = listOf(/* 이미지 데이터를 Painter 객체로 여기에 추가 */)
//    ImageSliderWithAnimation(images = images)
//}
//package com.zerodeg.feature_video.views
//
//import androidx.compose.foundation.gestures.rememberDraggableState
//import androidx.compose.foundation.layout.Box
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun DraggableHandle(state: TrimmerState, isStartHandle: Boolean) {
//    Box(
//        modifier = Modifier
//            .size(20.dp, 40.dp)
//            .background(Color.Gray)
//            .draggable(
//                orientation = Orientation.Horizontal,
//                state = rememberDraggableState { delta ->
//                    val newValue = if (isStartHandle) state.start + delta else state.end + delta
//                    if (newValue in state.min..state.max) {
//                        if (isStartHandle) {
//                            state.start = newValue.coerceAtMost(state.end)
//                        } else {
//                            state.end = newValue.coerceAtLeast(state.start)
//                        }
//                    }
//                }
//            )
//    )
//}
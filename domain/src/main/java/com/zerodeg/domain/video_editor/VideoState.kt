package com.zerodeg.domain.video_editor

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class VideoState(
    initialStart: Dp = 1.dp,
    initialEnd: Dp = 1.dp,
    initialWidth: Dp = 1.dp,
    totalTime: Int = 1,
    selectedStartTime: Int = 1,
    selectedEndTime: Int = 2,
    bitmap: Bitmap? = null
) {
    var start by mutableStateOf(initialStart)
    var end by mutableStateOf(initialEnd)
    var width by mutableStateOf(initialWidth)
    var totalTime by mutableIntStateOf(totalTime) // mil
    var selectedStartTime by mutableIntStateOf(selectedStartTime)
    var selectedEndTime by mutableIntStateOf(selectedEndTime)
    var bitmap by mutableStateOf(bitmap)
}
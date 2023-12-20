package com.zerodeg.domain.video_editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class VideoState(
    uri: Uri? = null,
    initialStart: Dp = -(1.dp),
    initialEnd: Dp = -(1.dp),
    initialWidth: Dp = -(1.dp),
    totalTime: Int = 1,
    selectedStartTime: Int = 1,
    selectedEndTime: Int = 2,
    selectedTime: Int = 1,
    bitmaps: List<Bitmap>? = null,
    bitmap: Bitmap? = null
) {
    var uri by mutableStateOf(uri)
    var start by mutableStateOf(initialStart)
    var end by mutableStateOf(initialEnd)
    var width by mutableStateOf(initialWidth)
    var totalTime by mutableIntStateOf(totalTime) // mil
    var startTime by mutableIntStateOf(selectedStartTime)
    var endTime by mutableIntStateOf(selectedEndTime)
    var selectedTime by mutableIntStateOf(selectedTime)
    var bitmapList by mutableStateOf(bitmaps)
    var bitmap by mutableStateOf(bitmap)
}
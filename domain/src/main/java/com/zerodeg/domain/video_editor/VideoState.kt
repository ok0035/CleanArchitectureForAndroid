package com.zerodeg.domain.video_editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class VideoState(
    uri: Uri? = null,
    filteredUri: Uri? = null,
    initialStart: Dp = -(1.dp),
    initialEnd: Dp = -(1.dp),
    initialWidth: Dp = -(1.dp),
    totalTime: Long = 1,
    selectedStartTime: Long = 1,
    selectedEndTime: Long = 2,
    selectedTime: Long = 1,
    playingTime: Long = 1,
    bitmaps: List<Bitmap>? = null,
    bitmap: Bitmap? = null
) {
    var uri by mutableStateOf(uri)
    var filteredUri by mutableStateOf(filteredUri)
    var start by mutableStateOf(initialStart)
    var end by mutableStateOf(initialEnd)
    var width by mutableStateOf(initialWidth)
    var totalTime by mutableLongStateOf(totalTime) // mil
    var startTime by mutableLongStateOf(selectedStartTime)
    var endTime by mutableLongStateOf(selectedEndTime)
    var selectedTime by mutableLongStateOf(selectedTime)
    var playingTime by mutableLongStateOf(playingTime)
    var bitmapList by mutableStateOf(bitmaps)
    var bitmap by mutableStateOf(bitmap)
}
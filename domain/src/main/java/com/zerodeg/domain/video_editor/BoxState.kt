package com.zerodeg.domain.video_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BoxState(initialStart: Float, initialEnd: Float, initialWidth: Int) {
    var start by mutableFloatStateOf(initialStart)
    var end by mutableFloatStateOf(initialEnd)
    var width by mutableIntStateOf(initialWidth)
}
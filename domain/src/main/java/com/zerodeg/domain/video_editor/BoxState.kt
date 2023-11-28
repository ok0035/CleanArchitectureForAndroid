package com.zerodeg.domain.video_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp

class BoxState(initialStart: Dp, initialEnd: Dp, initialWidth: Dp) {
    var start by mutableStateOf(initialStart)
    var end by mutableStateOf(initialEnd)
    var width by mutableStateOf(initialWidth)
}
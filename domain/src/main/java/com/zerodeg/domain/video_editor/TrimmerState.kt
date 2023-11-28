package com.zerodeg.domain.video_editor

class TrimmerState(val min:Float, val max: Float) {
    var start by mutableStateOf(min)
}
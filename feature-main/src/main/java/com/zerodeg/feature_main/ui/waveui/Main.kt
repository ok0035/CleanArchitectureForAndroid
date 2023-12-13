package com.zerodeg.feature_main.ui.waveui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

// 화면 상태를 정의합니다.
enum class ScreenState {
    Screen1, Screen2
}

@Composable
fun MainScreen() {
    var screenState by remember { mutableStateOf(ScreenState.Screen1) }

    when (screenState) {
        ScreenState.Screen1 -> WaveScreen(onScreenChange = { screenState = ScreenState.Screen2 })
        ScreenState.Screen2 -> Screen2()
    }
}

@Composable
fun WaveScreen(onScreenChange: () -> Unit) {
    WaveEffectCanvas(modifier = Modifier.fillMaxSize()) { offset ->
        // 터치 위치에 따라 화면 전환 조건 확인
        if (offset.x < 100f) {
            onScreenChange()
        }
    }
}

@Composable
fun Screen2() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Screen 2", fontSize = 24.sp)
    }
}
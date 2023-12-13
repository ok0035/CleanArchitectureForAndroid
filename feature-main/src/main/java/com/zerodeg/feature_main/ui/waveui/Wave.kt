package com.zerodeg.feature_main.ui.waveui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerodeg.feature_main.viewmodels.WaveViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaveEffectCanvas(
    modifier: Modifier = Modifier,
    onTouch: (Offset) -> Unit
) {
    var touchPoint by remember { mutableStateOf(Offset.Unspecified) }
    var isTouching by remember { mutableStateOf(false) }

    // 터치 이벤트를 감지하는 Modifier
    val touchModifier = modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                touchPoint = offset
                isTouching = true
                onTouch(offset)
                tryAwaitRelease()
                isTouching = false
            }
        )
    }

    Canvas(modifier = touchModifier) {
        if (isTouching && touchPoint != Offset.Unspecified) {
            val wavePath = createWavePath(size = size, phase = touchPoint.x)
            drawPath(path = wavePath, color = Color.Yellow.copy(alpha = 0.5f))
        }
    }
}

fun createWavePath(
    size: Size,
    amplitude: Float = 20f,
    frequency: Float = 10f,
    phase: Float
): Path {
    val path = Path()
    val yOffset = size.height / 2
    path.moveTo(0f, yOffset)

    for (x in 0..size.width.toInt()) {
        val y = amplitude * sin((2 * PI / frequency) * x + phase) + yOffset
        path.lineTo(x.toFloat(), y.toFloat())
    }

    return path
}

@Composable
fun WaveEffectScreen() {
    val viewModel: WaveViewModel = hiltViewModel()
    var touchX by remember { mutableFloatStateOf(Float.MAX_VALUE) }
    val wavePath by viewModel.wavePath.collectAsState(Path())
    val size = remember { MutableStateFlow(Size.Zero) }

    // 터치 이벤트를 처리하는 Modifier
    val touchModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, _ ->
            touchX = change.position.x
            viewModel.calculateWavePath(size.value.width, size.value.height, touchX)
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .then(touchModifier)
        .onSizeChanged {
            size.value = it.toSize()
        }) {
        drawPath(path = wavePath, color = Color.Blue.copy(alpha = 0.5f))
    }
}
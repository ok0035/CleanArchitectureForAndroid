package com.zerodeg.feature_main.viewmodels

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.sin

@HiltViewModel
class WaveViewModel @Inject constructor(): ViewModel() {

    private val _wavePath = MutableStateFlow<Path>(Path())
    val wavePath: StateFlow<Path> = _wavePath

    fun calculateWavePath(width: Float, height: Float, touchX: Float) {
        viewModelScope.launch {
            val newPath = Path().apply {
                // 여기서 물결 효과를 계산합니다.
                moveTo(width, 0f)
                lineTo(touchX, 0f)
                for (x in touchX.toInt() downTo 0 step 10) {
                    val y = sin((x / width * 2 * PI).toFloat()) * 50 + height / 2
                    lineTo(x.toFloat(), y)
                }
                lineTo(0f, height)
                lineTo(width, height)
                close()
            }
            _wavePath.emit(newPath)
        }
    }

}
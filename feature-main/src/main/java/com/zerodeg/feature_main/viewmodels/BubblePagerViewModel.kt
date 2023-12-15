package com.zerodeg.feature_main.viewmodels

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerodeg.feature_main.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.sin

@HiltViewModel
class BubblePagerViewModel @Inject constructor() : ViewModel() {


    val itemResList: List<Int> = listOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    )

    var currentIdx = 0

    val loadedImageList: MutableList<Int?> =
        mutableListOf(null, null, currentIdx, currentIdx + 1, currentIdx + 2)


}
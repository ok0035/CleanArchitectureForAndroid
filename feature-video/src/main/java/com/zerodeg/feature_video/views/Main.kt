package com.zerodeg.feature_video.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel

@Preview
@Composable
fun MainScreen() {

    val viewModel: VideoEditorViewModel = viewModel()

    var videoUri by remember { mutableStateOf<Uri?>(null) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VideoFrameImage(videoUri = videoUri)
        VideoCuttingBar(300, {frame ->

        })
        GalleryVideoPicker(onVideoPicked = { uri ->
            videoUri = uri
            viewModel.countVideoFrames(uri?.path ?: "")
        })
    }
}
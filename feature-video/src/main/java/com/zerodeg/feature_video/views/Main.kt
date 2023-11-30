package com.zerodeg.feature_video.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel

@Preview
@Composable
fun MainScreen() {

    val viewModel: VideoEditorViewModel = hiltViewModel()
    val context = LocalContext.current
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VideoFrameImage(videoUri = videoUri)
        VideoFrameSelector()
        GalleryVideoPicker(onVideoPicked = { uri ->
            videoUri = uri
            uri?.let {
                val path = viewModel.getRealPathFromURI(context, it) ?: return@let
                viewModel.updateVideoTotalTime(path)
            }
        })
    }
}
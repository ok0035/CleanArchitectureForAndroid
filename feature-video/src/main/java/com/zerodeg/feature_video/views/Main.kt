package com.zerodeg.feature_video.views

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.random.Random

@Preview
@Composable
fun MainScreen() {

    val viewModel: VideoEditorViewModel = hiltViewModel()

    val context = LocalContext.current
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf<Boolean>(false) }

    Box {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                GradientText()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
//                    VideoFrameImage(videoUri = videoUri)
                    VideoPlayer(uri = videoUri)
                }

                VideoFrameSelector()
                GalleryVideoPicker(onVideoPicked = { uri ->
                    uri?.let {
                        isLoading = true
                        val path = viewModel.getRealPathFromURI(context, it) ?: return@let
                        val newPath = viewModel.getTempPath(Random.nextInt().toString() + ".mp4")
                        Log.d("ENCODING", "New path -> $newPath")
                        viewModel.encodeVideoWithKeyframeInterval(path, newPath) { encodedUri ->
                            videoUri = encodedUri
                            viewModel.updateVideoTotalTime(path) { totalTime ->
                                val retriever = MediaMetadataRetriever()
                                retriever.setDataSource(context, videoUri)
                            }
                        }

                    }
                })
            }

        }

//        if (isLoading) {
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Black.copy(alpha = 0.3f))
//                    .clickable(
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() }) { /* 클릭 이벤트 차단 */ }
//            )
//
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }

    }
}

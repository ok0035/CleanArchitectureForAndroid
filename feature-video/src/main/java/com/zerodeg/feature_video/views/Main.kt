package com.zerodeg.feature_video.views

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel
import kotlin.contracts.contract

@Preview
@Composable
fun MainScreen() {

    val viewModel: VideoEditorViewModel = hiltViewModel()

    val context = LocalContext.current



    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {
            viewModel.isLoading = true
            val path = viewModel.getRealPathFromURI(it) ?: return@let
            val newPath = viewModel.getTempRandomPath(path.split(".").last())
            Log.d("ENCODING", "New path -> $newPath")
            viewModel.encodeVideoWithKeyframeInterval(path, newPath) { encodedUri ->
//                viewModel.updateSelectedVideoUri(encodedUri)
                viewModel.updateVideoTotalTime(path) { totalTime ->
//                    viewModel.selectedVideoUri.value?.let { uri ->
//                        val retriever = MediaMetadataRetriever()
//                        retriever.setDataSource(context, uri)
//                    }
                    viewModel.videoStateList[0].uri = encodedUri
                    viewModel.videoStateList[0].totalTime = totalTime.toInt()
                    viewModel.selectedVideoIdx.intValue = 0
                    viewModel.isLoading = false
                }
            }

        }
    }

    val videoPickerLauncher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.isLoading = true
            val path = viewModel.getRealPathFromURI(it) ?: return@let
            val newPath = viewModel.getTempRandomPath(path.split(".").last())
            Log.d("ENCODING", "New path -> $newPath")
            viewModel.encodeVideoWithKeyframeInterval(path, newPath) { encodedUri ->
//                viewModel.updateSelectedVideoUri(encodedUri)
                viewModel.updateVideoTotalTime(path) { totalTime ->
//                    viewModel.selectedVideoUri.value?.let { uri ->
//                        val retriever = MediaMetadataRetriever()
//                        retriever.setDataSource(context, uri)
//                    }
                    viewModel.videoStateList[1].uri = encodedUri
                    viewModel.videoStateList[1].totalTime = totalTime.toInt()
                    viewModel.selectedVideoIdx.intValue = 1
                    viewModel.isLoading = false
                }
            }

        }
    }

    val videoPickerLauncher3 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.isLoading = true
            val path = viewModel.getRealPathFromURI(it) ?: return@let
            val newPath = viewModel.getTempRandomPath(path.split(".").last())
            Log.d("ENCODING", "New path -> $newPath")
            viewModel.encodeVideoWithKeyframeInterval(path, newPath) { encodedUri ->
                viewModel.updateVideoTotalTime(path) { totalTime ->
                    viewModel.videoStateList[2].uri = encodedUri
                    viewModel.videoStateList[2].totalTime = totalTime.toInt()
                    viewModel.selectedVideoIdx.intValue = 2
                    viewModel.isLoading = false
                }
            }

        }
    }


    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val editView = createRef()
        val editMenuRef = createRef()
        val selectButtons = createRef()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .background(Color.Black)
                .constrainAs(editView) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(selectButtons.top)
                },
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

//                GradientText()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center

                ) {
//                    VideoPlayer(state = viewModel.videoStateList[viewModel.selectedVideoIdx.intValue])
                    when (viewModel.selectedVideoIdx.intValue) {
                        0 -> {
//                            Log.d("VideoPlayer", "state -> ${viewModel.videoState1.uri?.path}")
                            VideoPlayer(viewModel.videoStateList[0])
                        }
                        1 -> {
//                            Log.d("VideoPlayer", "state2 -> ${viewModel.videoState2.uri?.path}")
                            VideoPlayer(viewModel.videoStateList[1])
                        }
                        2 -> {
//                            Log.d("VideoPlayer", "state3 -> ${viewModel.videoState3.uri?.path}")
                            VideoPlayer(viewModel.videoStateList[2])
                        }

                    }
                }

            }

        }

        ConstraintLayout(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 20.dp, bottom = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .constrainAs(selectButtons) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(editMenuRef.top)
                }
        ) {

            val (sel1, sel2, sel3) = createRefs()

            Text(
                text = "SELECT1",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(sel1) {
                        start.linkTo(parent.start)
                        end.linkTo(sel2.start)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {
                        if (viewModel.videoStateList[0].uri == null) {
                            videoPickerLauncher.launch("video/*")
                        } else {
                            viewModel.selectedVideoIdx.intValue = 0
                        }
                    },

                )

            Text(
                text = "SELECT2",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(sel2) {
                        start.linkTo(sel1.end)
                        end.linkTo(sel3.start)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {
                        if (viewModel.videoStateList[1].uri == null)
                            videoPickerLauncher2.launch("video/*")
                        else viewModel.selectedVideoIdx.intValue = 1
                    },

                )

            Text(
                text = "SELECT3",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(sel3) {
                        start.linkTo(sel2.end)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {
                        if (viewModel.videoStateList[2].uri == null)
                            videoPickerLauncher3.launch("video/*")
                        else viewModel.selectedVideoIdx.intValue = 2
                    },

                )

        }

        ConstraintLayout(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .constrainAs(editMenuRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {

            val (cut, merge, text) = createRefs()

            Text(
                text = "TRIM",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(cut) {
                        start.linkTo(parent.start)
                        end.linkTo(merge.start)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {
                        viewModel.trimVideo()
                    }
            )

            Text(
                text = "MERGE",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(merge) {
                        start.linkTo(cut.end)
                        end.linkTo(text.start)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {

                    }
            )

            Text(
                text = "TEXT",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black)
                    .constrainAs(text) {
                        start.linkTo(merge.end)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {

                    },
            )

        }

        if (viewModel.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) { /* 클릭 이벤트 차단 */ },
                contentAlignment = Alignment.Center
            ) {


                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val (loadingIconRef, loadingMsgRef) = createRefs()

                    CircularProgressIndicator(
                        modifier = Modifier.constrainAs(loadingIconRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                    )
                    Text(
                        modifier = Modifier.constrainAs(loadingMsgRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(loadingIconRef.bottom, 20.dp)
                        },
                        text = viewModel.loadingMsg.value,
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }

            }
        }

    }
}

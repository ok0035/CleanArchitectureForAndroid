package com.zerodeg.feature_video.views

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerodeg.feature_video.viewmodels.VideoEditorViewModel


@Composable
fun Main() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("video_result") { VideoResultScreen(navController) }
    }
}

@Preview
@Composable
fun MainScreen(navController: NavController) {

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
                viewModel.updateVideoTotalTime(path) { totalTime ->
                    viewModel.videoStateList[0].uri = encodedUri
                    viewModel.videoStateList[0].totalTime = totalTime
                    viewModel.loadBitmaps(uri,
                        onSuccess = { bitmaps ->
                            viewModel.videoStateList[0].bitmapList = bitmaps
                        }, onComplete = {

                        })
                    viewModel.selectedVideoIdx.intValue = 0
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
                viewModel.updateVideoTotalTime(path) { totalTime ->
                    viewModel.videoStateList[1].uri = encodedUri
                    viewModel.videoStateList[1].totalTime = totalTime
                    viewModel.loadBitmaps(uri,
                        onSuccess = { bitmaps ->
                            viewModel.videoStateList[1].bitmapList = bitmaps
                        }, onComplete = {
                            viewModel.isLoading = false
                        })
                    viewModel.selectedVideoIdx.intValue = 1
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
                    viewModel.videoStateList[2].totalTime = totalTime
                    viewModel.selectedVideoIdx.intValue = 2
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, uri)
                    viewModel.loadBitmaps(uri,
                        onSuccess = { bitmaps ->
                            viewModel.videoStateList[2].bitmapList = bitmaps
                        }, onComplete = {
                            viewModel.isLoading = false
                        })
                }
            }

        }
    }


    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val (editView, editMenuRef, selectButtons) = createRefs()

        Box(
            modifier = Modifier
                .background(Color.Black)
                .constrainAs(editView) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    bottom.linkTo(selectButtons.top, margin = 40.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {

            when (viewModel.selectedVideoIdx.intValue) {
                0 -> VideoEditor(viewModel.videoStateList[0], navController = navController)
                1 -> VideoEditor(viewModel.videoStateList[1], navController = navController)
                2 -> VideoEditor(viewModel.videoStateList[2], navController = navController)
            }
        }

        ConstraintLayout(
            modifier = Modifier
                .background(Color.Black)
                .constrainAs(selectButtons) {
                    start.linkTo(parent.start, margin = 40.dp)
                    end.linkTo(parent.end, margin = 40.dp)
                    bottom.linkTo(editMenuRef.top, margin = 60.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {

            val (sel1, sel2, sel3) = createRefs()

            SelectVideoView(modifier = Modifier
                .constrainAs(sel1) {
                    start.linkTo(parent.start)
                    end.linkTo(sel2.start, 8.dp)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(100.dp)
                }, viewModel.videoStateList[0].bitmapList?.get(1)?.asImageBitmap(),
                isSelect = viewModel.selectedVideoIdx.intValue == 0,
                onClick = {
                    if (viewModel.videoStateList[0].uri == null) {
                        videoPickerLauncher.launch("video/*")
//                        navController.navigate("video_result")
                    } else {
                        viewModel.selectedVideoIdx.intValue = 0
                    }
                }
            )

            SelectVideoView(modifier = Modifier
                .constrainAs(sel2) {
                    start.linkTo(sel1.end)
                    end.linkTo(sel3.start, 8.dp)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(100.dp)
                }, viewModel.videoStateList[1].bitmapList?.get(1)?.asImageBitmap(),
                isSelect = viewModel.selectedVideoIdx.intValue == 1,
                onClick = {
                    if (viewModel.videoStateList[1].uri == null) {
                        videoPickerLauncher2.launch("video/*")
                    } else {
                        viewModel.selectedVideoIdx.intValue = 1
                    }
                }
            )

            SelectVideoView(modifier = Modifier
                .constrainAs(sel3) {
                    start.linkTo(sel2.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(100.dp)
                }, viewModel.videoStateList[2].bitmapList?.get(1)?.asImageBitmap(),
                isSelect = viewModel.selectedVideoIdx.intValue == 2,
                onClick = {
                    if (viewModel.videoStateList[2].uri == null) {
                        videoPickerLauncher3.launch("video/*")
                    } else {
                        viewModel.selectedVideoIdx.intValue = 2
                    }
                }
            )

        }

        ConstraintLayout(
            modifier = Modifier
                .background(Color.Black)
                .constrainAs(editMenuRef) {
                    start.linkTo(parent.start, margin = 40.dp)
                    end.linkTo(parent.end, margin = 40.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(50.dp)
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
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .clickable {

                    }
            )

//            Text(
//                text = "TEXT",
//                color = Color.White,
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .background(Color.Black)
//                    .constrainAs(text) {
//                        start.linkTo(merge.end)
//                        end.linkTo(parent.end)
//                        bottom.linkTo(parent.bottom)
//                        width = Dimension.fillToConstraints
//                    }
//                    .clickable {
//
//                    },
//            )

        }

        if (viewModel.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
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

//    DisposableEffect(Unit) {
//        onDispose {
//            viewModel.deleteCacheData()
////            exoPlayer.release()
//        }
//    }
}

@Composable
fun VideoResultScreen(navController: NavController) {

}

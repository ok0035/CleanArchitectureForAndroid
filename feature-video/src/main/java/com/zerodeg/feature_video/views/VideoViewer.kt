package com.zerodeg.feature_video.views

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GalleryVideoPicker(onVideoPicked: (Uri?) -> Unit) {
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onVideoPicked(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Column을 부모 컨테이너 크기만큼 채움
            .padding(16.dp), // 패딩 추가
        verticalArrangement = Arrangement.Bottom, // 세로 방향으로 하단 정렬
        horizontalAlignment = Alignment.CenterHorizontally // 가로 방향으로 중앙 정렬
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Row를 부모 컨테이너 너비만큼 채움
            horizontalArrangement = Arrangement.Center // 가로 방향으로 중앙 정렬
        ) {
            Button(
                onClick = { videoPickerLauncher.launch("video/*") },
            ) {
                Text("Select Video")
            }
        }
    }

}

@Composable
fun VideoFrameImage(videoUri: Uri?) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    LaunchedEffect(videoUri) {
        videoUri?.let { uri ->
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val frameBitmap = retriever.frameAtTime
                bitmap = frameBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }

    bitmap?.let {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black)
                .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(300.dp)
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(12.dp)) // 여기에 clip 적용
                    .background(Color.Black)
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Video Frame",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }


}

@Preview
@Composable
fun VideoSelector() {
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    GalleryVideoPicker(onVideoPicked = { uri ->
        videoUri = uri
    })

    VideoFrameImage(videoUri = videoUri)

    // 다른 컴포저블들...
}
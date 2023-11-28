package com.zerodeg.app_video_editor.views.main

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zerodeg.app_video_editor.ui.theme.CleanArchitectureTheme
import com.zerodeg.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanArchitectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        setPermission()

    }

    private fun setPermission() {
        permissionManager.setupPermissions(

            onPermissionResult = { permission, isGranted ->

                when (permission) {
                    Manifest.permission.READ_MEDIA_VIDEO -> {
                        if (isGranted) {
                            Log.d("MAIN", "비디오 앍가 권한 승인")
                        } else {
                            Log.d("MAIN", "비디오 앍가 권한 거절")
                        }
                    }

                    Manifest.permission.READ_MEDIA_IMAGES -> {
                        if (isGranted) {
                            Log.d("MAIN", "이미지 읽기 권한 승인")
                        } else {
                            Log.d("MAIN", "이미지 읽기 권한 거절")
                        }
                    }

                    Manifest.permission.READ_MEDIA_AUDIO -> {
                        if (isGranted) {
                            Log.d("MAIN", "오디오 읽기 권한 승인")
                        } else {
                            Log.d("MAIN", "오디오 읽기 권한 거절")
                        }
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (isGranted) {
                            Log.d("MAIN", "읽기 권한 승인")
                        } else {
                            Log.d("MAIN", "읽기 권한 거절")
                        }
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (isGranted) {
                            Log.d("MAIN", "쓰기 권한 승인")
                        } else {
                            Log.d("MAIN", "쓰기 권한 거절")
                        }
                    }
                }

            },
            onAllPermissionGranted = {
                Log.d("MAIN", "요청한 모든 권한 승인")
            }
        )

//        permissionManager.setupFileWriteRequest(
//            onFileWriteGranted = {uri ->
//                Log.d("MAIN", "쓰기 권한 승인")
//            },
//            onFileWriteDenied = {
//                Log.d("MAIN", "쓰기 권한 거절")
//            }
//        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.requestPermissions(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO
            )
//            permissionManager.requestFileWritePermission(
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//            )
        } else {
            permissionManager.requestPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE // Android10 이상에서는 필요하지 않음
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanArchitectureTheme {
        Greeting("Android")
    }
}
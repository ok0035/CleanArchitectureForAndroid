package com.zerodeg.feature_video.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoUtils @Inject constructor(
    private val context: Context
) {
    fun getTempFilePath(fileName: String): String {
        val directory = context.filesDir // 내부 저장소의 파일 디렉토리
        val file = File(directory, fileName)
        return file.absolutePath
    }

    fun getRealPathFromURI(contentUri: Uri): String? {
        return try {
            // 임시 파일 생성
            val tempFile = File(context.cacheDir, "temp_video.mp4")
            tempFile.createNewFile()

            // Uri에서 InputStream을 얻고, 이를 임시 파일에 복사
            context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                FileOutputStream(tempFile).use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
            }

            // 임시 파일의 경로 반환
            tempFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}
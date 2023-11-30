package com.zerodeg.feature_video.utils

import android.content.Context
import java.io.File
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

}
package com.zerodeg.feature_video.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.ReturnCode
import com.arthenica.ffmpegkit.SessionState
import com.zerodeg.domain.video_editor.VideoState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoUtils @Inject constructor(
    private val context: Context
) {

    /**
     * 비디오를 잘라내는 메소드
     *
     * @param inputPath  원본 비디오 파일 경로
     * @param outputPath 저장할 비디오 파일 경로
     * @param start      시작 시간 (초)
     * @param end        종료 시간 (초)
     */
    fun trimVideo(
        inputPath: String?,
        start: Double,
        end: Double,
        onError: () -> Unit,
        onSuccess: (outputPath: String) -> Unit
    ) {
        // 비디오 잘라내기를 위한 FFmpeg 명령어 구성
        val encoder = "libx264"
        val outputPath = getTempFilePath(inputPath?.split(".")?.last() ?: "mp4")
//        val cmd = String.format("-i %s -ss %d -to %d -c copy %s", inputPath, start, end, outputPath)
        val cmd = "-i $inputPath -ss $start -to $end -g 30 -c:v $encoder -c:a aac $outputPath"

        Log.d("trim", "start -> $start end -> $end")

        // FFmpegKit을 사용하여 명령어 실행
        FFmpegKit.executeAsync(cmd) { session: FFmpegSession? ->
            // 세션이 완료되면 실행될 코드
            if (ReturnCode.isSuccess(session?.returnCode)) {
                // 성공적으로 처리됨
                println("Video trimmed successfully.")
                onSuccess(outputPath)

            } else {
                // 처리 중 오류 발생
                println("Error in trimming video.")
                onError()
            }
        }
    }

    fun mergeVideos(
        videoPaths: List<VideoState>,
        outputFile: String,
        onError: () -> Unit,
        onSuccess: (uri: Uri) -> Unit
    ) {
        val pathList = mutableListOf<String>()
        videoPaths.forEach {
            pathList.add(it.uri?.path ?: "")
        }
        val fileList = createFileList(pathList)
        val command = "ffmpeg -f concat -safe 0 -i $fileList -c copy $outputFile"

        FFmpegKit.executeAsync(command) { session ->
            val returnCode = session.returnCode
            if (returnCode.isValueSuccess) {
                // 합쳐진 비디오 파일의 Uri를 생성
                val mergedVideoUri = Uri.parse(outputFile)
                onSuccess(mergedVideoUri)
            } else {
                // 실패 처리 로직
                onError()
            }
        }
    }

    fun updateVideoTotalTime(
        videoPath: String, onCompleteListener: ((totalTime: Long) -> Unit)? = null
    ) {
        val cmd =
            "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 $videoPath"

        Log.d("FFmpeg", "FFmpegKit Cmd -> $cmd")
        FFprobeKit.executeAsync(
            cmd,
            { session ->
                val output = session.output
                val frameTime = output?.trim()?.toDoubleOrNull()

                if (frameTime != null) {
                    Log.d("FFmpegKit", "success: $frameTime ${(frameTime * 1000).toInt()}")
//                    videoStateList.totalTime = (frameTime * 1000).toInt()
                    onCompleteListener?.invoke((frameTime * 1000).toLong())
                } else {
                    // 오류 처리
                    Log.e("FFmpegKit", "Failed ${session.output}")
                }
            },
            { log ->
                // 로그 처리
                Log.d("FFmpegKitLog", log.message)
            },

            )
    }


    fun encodeVideoWithKeyframeInterval(
        videoPath: String,
        outputPath: String,
        onCompletion: (uri: Uri) -> Unit
    ) {
//        val cmd = "-i $videoPath -c:v libx264 -preset ultrafast -x264opts keyint=1:min-keyint=1 -b:v 5000k $outputPath"
        val cmd = "-i $videoPath -g 30 -c:v libx264 -c:a aac $outputPath"
        FFmpegKit.executeAsync(cmd) { session ->
            if (session.returnCode.isValueSuccess) {
                Log.d("FFmpegKit", "Encoding successful")
                val uri = Uri.parse(outputPath)
                onCompletion(uri)
            } else {
                Log.e("FFmpegKit", "Encoding failed")
            }
        }
    }

    fun getTempFilePath(extension: String): String {
        val directory = context.cacheDir // 내부 저장소의 파일 디렉토리
        val file = File(directory, generateRandomString(10) + ".$extension")
        return file.absolutePath
    }

    fun getMediaSource(uri: Uri): ProgressiveMediaSource {

        return ProgressiveMediaSource.Factory(getDataSourceFactory())
            .createMediaSource(MediaItem.fromUri(uri))

    }

    fun getLoadControl(
        minBufferMs: Int = 15,
        maxBufferMs: Int = 30,
        bufferForPlayBackMs: Int = 2,
        bufferForPlaybackAfterRebufferMs: Int = 5,
        backBufferDurationMs: Int = 10,
        retainBackBufferFromKeyframe: Boolean = true
    ): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                minBufferMs, // 최소 버퍼링 시간 (15초)
                maxBufferMs, // 최대 버퍼링 시간 (30초)
                bufferForPlayBackMs,  // 재생을 위한 버퍼링 시간 (2.5초)
                bufferForPlaybackAfterRebufferMs   // 재버퍼링 후 재생을 위한 시간 (5초)
            )
            .setBackBuffer(
                backBufferDurationMs,
                retainBackBufferFromKeyframe
            ) // 백버퍼 길이 (10초), 재생 중에도 유지
            .build()

    }

    fun overlayHandwriting() {
        // 입력 비디오 파일과 손글씨 이미지 파일의 경로
        val inputVideoPath = "/path/to/input.mp4"
        val handwritingImagePath = "/path/to/handwriting.png"

        // 손글씨가 오버레이 될 시간 구간 (예: 10초부터 20초까지)
        val startTime = 10  // 시작 시간(초)
        val endTime = 20   // 종료 시간(초)

        // FFmpeg 명령어 구성
        val command = "-i $inputVideoPath -i $handwritingImagePath -filter_complex " +
                "[0:v][1:v] overlay=25:25:enable='between(t,$startTime,$endTime)' " +
                "-pix_fmt yuv420p -c:a copy /path/to/output.mp4"

        // FFmpeg 명령어 실행
        FFmpegKit.executeAsync(command) { session ->
            val state = session.state
            val returnCode = session.returnCode

            // 작업 완료 확인
            if (state == SessionState.COMPLETED && returnCode.isValueSuccess) {
                println("FFmpeg process succeeded")
            } else {
                println("FFmpeg process failed with return code: ${returnCode.value}")
            }
        }
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

    // 임시 파일 생성 및 데이터 쓰기
    fun createTempFile(data: ByteArray, fileName: String): File {
        val tempFile = File.createTempFile(fileName, null, context.cacheDir)
        FileOutputStream(tempFile).use { outputStream ->
            outputStream.write(data)
        }
        return tempFile
    }

    // 내부 저장소의 임시 파일 모두 삭제
    fun deleteTempFiles() {
        context.cacheDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }

    private fun getDataSourceFactory(): DataSource.Factory {
        val defaultDataSourceFactory = DefaultDataSource.Factory(context)
        return DefaultDataSource.Factory(
            context,
            defaultDataSourceFactory
        )
    }

    private fun generateRandomString(length: Int = 10): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun createFileList(videoPaths: List<String>): String {
        val fileList = File.createTempFile("filelist", ".txt")
        fileList.bufferedWriter().use { writer ->
            videoPaths.forEach { path ->
                writer.write("file '$path'\n")
            }
        }
        return fileList.absolutePath
    }

}

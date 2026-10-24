package com.microbond.app.screens.threadscreen.threadscreenwidgets

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File

/**
 * State holder for voice recording interactions within the ThreadScreen.
 */
class ThreadVoiceRecordingState(
    val isVoiceRecording: Boolean,
    val isVoiceRecordingLocked: Boolean,
    val recordingDurationSeconds: Int,
    val onRecordingStateChange: (Boolean) -> Unit,
    val onRecordingStart: () -> Unit,
    val onRecordingFinish: (cancelled: Boolean) -> Unit
)

/**
 * Manages audio recording lifecycle, microphone permissions, timer ticking,
 * and audio file generation for voice notes.
 */
@Composable
fun rememberThreadVoiceRecorder(
    onVoiceNoteRecorded: (voiceUri: String, durationSeconds: Int) -> Unit
): ThreadVoiceRecordingState {
    val context = LocalContext.current
    var isVoiceRecording by remember { mutableStateOf(false) }
    var isVoiceRecordingLocked by remember { mutableStateOf(false) }
    var isRecordingPressActive by remember { mutableStateOf(false) }
    var recordingStartedAt by remember { mutableLongStateOf(0L) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    var voiceRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var voiceRecordingFile by remember { mutableStateOf<File?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            voiceRecorder?.release()
            voiceRecordingFile?.delete()
        }
    }

    fun startRecording() {
        if (voiceRecorder != null || !isRecordingPressActive) return
        val outputFile = File(context.cacheDir, "voice-${System.currentTimeMillis()}.m4a")
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
        else @Suppress("DEPRECATION") MediaRecorder()
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioSamplingRate(44_100)
            recorder.setAudioEncodingBitRate(96_000)
            recorder.setOutputFile(outputFile.absolutePath)
            recorder.prepare()
            recorder.start()
            voiceRecorder = recorder
            voiceRecordingFile = outputFile
            recordingStartedAt = SystemClock.elapsedRealtime()
            recordingDurationSeconds = 0
            isVoiceRecording = true
            isVoiceRecordingLocked = false
        } catch (error: Exception) {
            recorder.release()
            outputFile.delete()
            voiceRecorder = null
            voiceRecordingFile = null
            isVoiceRecording = false
            isVoiceRecordingLocked = false
            isRecordingPressActive = false
            Toast.makeText(context, "Could not start voice recording: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun finishRecording(cancelled: Boolean) {
        isRecordingPressActive = false
        isVoiceRecordingLocked = false
        val recorder = voiceRecorder ?: return
        val outputFile = voiceRecordingFile
        try {
            recorder.stop()
            if (!cancelled && outputFile != null && outputFile.length() > 0L) {
                val duration = maxOf(1, ((SystemClock.elapsedRealtime() - recordingStartedAt) / 1_000L).toInt())
                onVoiceNoteRecorded(outputFile.toURI().toString(), duration)
            } else {
                outputFile?.delete()
            }
        } catch (_: RuntimeException) {
            outputFile?.delete()
            if (!cancelled) Toast.makeText(context, "Voice recording was too short to send.", Toast.LENGTH_SHORT).show()
        } finally {
            try { recorder.reset() } finally { recorder.release() }
            voiceRecorder = null
            voiceRecordingFile = null
            isVoiceRecording = false
            recordingDurationSeconds = 0
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startRecording()
        else Toast.makeText(context, "Microphone permission required for voice notes", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(isVoiceRecording) {
        if (isVoiceRecording) {
            while (isVoiceRecording) {
                recordingDurationSeconds = ((SystemClock.elapsedRealtime() - recordingStartedAt) / 1000).toInt()
                delay(200)
            }
        }
    }

    return remember(isVoiceRecording, isVoiceRecordingLocked, recordingDurationSeconds) {
        ThreadVoiceRecordingState(
            isVoiceRecording = isVoiceRecording,
            isVoiceRecordingLocked = isVoiceRecordingLocked,
            recordingDurationSeconds = recordingDurationSeconds,
            onRecordingStateChange = { isRecordingPressActive = it },
            onRecordingStart = {
                isRecordingPressActive = true
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) startRecording()
                else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            onRecordingFinish = ::finishRecording
        )
    }
}

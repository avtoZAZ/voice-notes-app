package com.avtozaz.voicenotes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avtozaz.voicenotes.service.VoiceRecordingService
import com.avtozaz.voicenotes.ui.MainScreen
import com.avtozaz.voicenotes.ui.theme.VoiceNotesTheme
import com.avtozaz.voicenotes.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecording by mutableStateOf(false)
    private var noteViewModel: NoteViewModel? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, will be used when starting recording
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "Speech recognition not available on this device",
                Toast.LENGTH_LONG
            ).show()
        }

        setContent {
            noteViewModel = viewModel<NoteViewModel>()
            val notes by noteViewModel!!.notes.collectAsStateWithLifecycle()

            VoiceNotesTheme {
                MainScreen(
                    notes = notes,
                    onRecordClick = {
                        checkPermissionAndRecord(VoiceRecordingService.MODE_VAD)
                    },
                    onRecordLongPress = {
                        checkPermissionAndRecord(VoiceRecordingService.MODE_HOLD)
                    },
                    onRecordRelease = {
                        VoiceRecordingService.stopRecording(this)
                        isRecording = false
                    },
                    onCompleteNote = { note ->
                        noteViewModel?.completeNote(note)
                    },
                    isRecording = isRecording
                )
            }
        }
    }

    private fun checkPermissionAndRecord(mode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceRecording(mode)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceRecording(mode: Int) {
        isRecording = true
        VoiceRecordingService.startRecording(this, mode)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

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
            startVoiceRecognition()
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
                        if (isRecording) {
                            stopVoiceRecognition()
                        } else {
                            checkPermissionAndRecord()
                        }
                    },
                    onCompleteNote = { note ->
                        noteViewModel?.completeNote(note)
                    },
                    isRecording = isRecording
                )
            }
        }
    }

    private fun checkPermissionAndRecord() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceRecognition()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceRecognition() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(recognitionListener)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        isRecording = true
        speechRecognizer?.startListening(intent)
    }

    private fun stopVoiceRecognition() {
        isRecording = false
        speechRecognizer?.stopListening()
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            // Ready to record
        }

        override fun onBeginningOfSpeech() {
            // User started speaking
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Volume changed
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Buffer received
        }

        override fun onEndOfSpeech() {
            isRecording = false
        }

        override fun onError(error: Int) {
            isRecording = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            
            if (error != SpeechRecognizer.ERROR_NO_MATCH && 
                error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(results: Bundle?) {
            isRecording = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                if (recognizedText.isNotBlank()) {
                    noteViewModel?.addNote(recognizedText)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // Partial results available
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Reserved for future events
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

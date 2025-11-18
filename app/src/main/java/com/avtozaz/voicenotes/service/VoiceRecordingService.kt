package com.avtozaz.voicenotes.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.avtozaz.voicenotes.MainActivity
import com.avtozaz.voicenotes.R
import com.avtozaz.voicenotes.data.NoteDatabase
import com.avtozaz.voicenotes.data.VoiceNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class VoiceRecordingService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var audioFile: File? = null
    private var isRecording = false
    private var recordingMode = MODE_HOLD // Default mode
    
    // Voice Activity Detection
    private val vadHandler = Handler(Looper.getMainLooper())
    private var lastAmplitude = 0
    private var silenceCount = 0
    private val SILENCE_THRESHOLD = 500 // Amplitude threshold
    private val SILENCE_DURATION = 15 // Number of checks (~1.5 seconds)
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val TAG = "VoiceRecordingService"
        const val CHANNEL_ID = "voice_recording_channel"
        const val NOTIFICATION_ID = 1
        
        const val ACTION_START_RECORDING = "com.avtozaz.voicenotes.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.avtozaz.voicenotes.STOP_RECORDING"
        const val EXTRA_MODE = "recording_mode"
        
        const val MODE_HOLD = 0 // Record while held
        const val MODE_VAD = 1 // Record until silence detected
        
        fun startRecording(context: Context, mode: Int = MODE_HOLD) {
            val intent = Intent(context, VoiceRecordingService::class.java).apply {
                action = ACTION_START_RECORDING
                putExtra(EXTRA_MODE, mode)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopRecording(context: Context) {
            val intent = Intent(context, VoiceRecordingService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                recordingMode = intent.getIntExtra(EXTRA_MODE, MODE_HOLD)
                startRecording()
            }
            ACTION_STOP_RECORDING -> {
                stopRecording()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startRecording() {
        if (isRecording) return
        
        try {
            // Create audio file
            val audioDir = File(filesDir, "audio")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            audioFile = File(audioDir, "recording_${System.currentTimeMillis()}.3gp")
            
            // Start foreground service
            startForeground(NOTIFICATION_ID, createNotification())
            
            // Initialize MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFile?.absolutePath)
                
                try {
                    prepare()
                    start()
                    isRecording = true
                    
                    // Start VAD monitoring if in VAD mode
                    if (recordingMode == MODE_VAD) {
                        startVoiceActivityDetection()
                    }
                    
                    Log.d(TAG, "Recording started: ${audioFile?.absolutePath}")
                } catch (e: IOException) {
                    Log.e(TAG, "Recording preparation failed", e)
                    stopSelf()
                }
            }
            
            // Start speech recognition
            startSpeechRecognition()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            stopSelf()
        }
    }

    private fun startVoiceActivityDetection() {
        vadHandler.postDelayed(object : Runnable {
            override fun run() {
                if (!isRecording) return
                
                try {
                    val amplitude = mediaRecorder?.maxAmplitude ?: 0
                    
                    if (amplitude < SILENCE_THRESHOLD) {
                        silenceCount++
                        if (silenceCount >= SILENCE_DURATION) {
                            Log.d(TAG, "Silence detected, stopping recording")
                            stopRecording()
                            return
                        }
                    } else {
                        silenceCount = 0
                    }
                    
                    lastAmplitude = amplitude
                    vadHandler.postDelayed(this, 100) // Check every 100ms
                } catch (e: Exception) {
                    Log.e(TAG, "VAD error", e)
                }
            }
        }, 100)
    }

    private fun startSpeechRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.w(TAG, "Speech recognition not available")
            return
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener)
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer?.startListening(intent)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: android.os.Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        
        override fun onError(error: Int) {
            Log.w(TAG, "Speech recognition error: $error")
        }
        
        override fun onResults(results: android.os.Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognizedText = matches[0]
                saveNote(recognizedText)
            }
        }
        
        override fun onPartialResults(partialResults: android.os.Bundle?) {}
        override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
    }

    private fun stopRecording() {
        if (!isRecording) return
        
        isRecording = false
        vadHandler.removeCallbacksAndMessages(null)
        
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            Log.d(TAG, "Recording stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
        }
        
        stopForeground(true)
        stopSelf()
    }

    private fun saveNote(text: String) {
        if (text.isBlank()) return
        
        serviceScope.launch {
            try {
                val database = NoteDatabase.getDatabase(applicationContext)
                val note = VoiceNote(
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    audioFilePath = audioFile?.absolutePath
                )
                database.noteDao().insert(note)
                Log.d(TAG, "Note saved: $text")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save note", e)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Voice Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Recording voice notes"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording voice note")
            .setContentText("Tap to open app")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        }
    }
}

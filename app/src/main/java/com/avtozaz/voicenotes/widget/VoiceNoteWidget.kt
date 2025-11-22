package com.avtozaz.voicenotes.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.avtozaz.voicenotes.R
import com.avtozaz.voicenotes.service.VoiceRecordingService

class VoiceNoteWidget : AppWidgetProvider() {
    
    companion object {
        const val ACTION_START_RECORDING = "com.avtozaz.voicenotes.widget.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.avtozaz.voicenotes.widget.STOP_RECORDING"
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_START_RECORDING -> {
                // Start recording via service (VAD mode for widget - auto-stops on silence)
                VoiceRecordingService.startRecording(context, VoiceRecordingService.MODE_VAD)
            }
            ACTION_STOP_RECORDING -> {
                // Stop recording
                VoiceRecordingService.stopRecording(context)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Widget enabled for the first time
    }

    override fun onDisabled(context: Context) {
        // Last widget instance removed
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Create intent for starting recording
    val startIntent = Intent(context, VoiceNoteWidget::class.java).apply {
        action = VoiceNoteWidget.ACTION_START_RECORDING
    }
    val startPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        startIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val views = RemoteViews(context.packageName, R.layout.voice_note_widget)
    views.setOnClickPendingIntent(R.id.widget_button, startPendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

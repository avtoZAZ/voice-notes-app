package com.avtozaz.voicenotes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avtozaz.voicenotes.data.VoiceNote
import com.avtozaz.voicenotes.ui.theme.NothingBlack
import com.avtozaz.voicenotes.ui.theme.NothingDarkGray
import com.avtozaz.voicenotes.ui.theme.NothingMediumGray
import com.avtozaz.voicenotes.ui.theme.NothingRed
import com.avtozaz.voicenotes.ui.theme.NothingWhite
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    notes: List<VoiceNote>,
    onRecordClick: () -> Unit,
    onCompleteNote: (VoiceNote) -> Unit,
    isRecording: Boolean
) {
    Scaffold(
        containerColor = NothingBlack,
        floatingActionButton = {
            RecordButton(
                isRecording = isRecording,
                onClick = onRecordClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "voice notes",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = NothingWhite,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes yet.\nTap the microphone to record.",
                        color = NothingWhite.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = notes,
                        key = { it.id }
                    ) { note ->
                        var isVisible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = isVisible,
                            exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                        ) {
                            NoteCard(
                                note = note,
                                onComplete = {
                                    isVisible = false
                                    kotlinx.coroutines.DelayKt
                                    // Delay deletion to allow animation to finish
                                    androidx.compose.runtime.LaunchedEffect(Unit) {
                                        kotlinx.coroutines.delay(300)
                                        onCompleteNote(note)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: VoiceNote,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = NothingDarkGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.text,
                    color = NothingWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(note.timestamp),
                    color = NothingWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }

            IconButton(
                onClick = onComplete,
                modifier = Modifier
                    .background(NothingMediumGray, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = NothingRed
                )
            }
        }
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit
) {
    val scale by if (isRecording) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    FloatingActionButton(
        onClick = onClick,
        containerColor = NothingRed,
        contentColor = NothingWhite,
        modifier = Modifier
            .scale(scale)
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Record",
            modifier = Modifier.size(32.dp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

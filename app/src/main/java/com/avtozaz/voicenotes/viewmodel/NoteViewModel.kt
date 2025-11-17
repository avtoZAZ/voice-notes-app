package com.avtozaz.voicenotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avtozaz.voicenotes.data.NoteDatabase
import com.avtozaz.voicenotes.data.VoiceNote
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao = NoteDatabase.getDatabase(application).noteDao()

    val notes: StateFlow<List<VoiceNote>> = noteDao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(text: String) {
        viewModelScope.launch {
            noteDao.insert(VoiceNote(text = text))
        }
    }

    fun completeNote(note: VoiceNote) {
        viewModelScope.launch {
            noteDao.delete(note.id)
        }
    }
}

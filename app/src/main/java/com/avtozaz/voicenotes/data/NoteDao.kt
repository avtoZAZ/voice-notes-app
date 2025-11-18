package com.avtozaz.voicenotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM voice_notes WHERE isCompleted = 0 ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<VoiceNote>>

    @Insert
    suspend fun insert(note: VoiceNote)

    @Update
    suspend fun update(note: VoiceNote)

    @Query("DELETE FROM voice_notes WHERE id = :noteId")
    suspend fun delete(noteId: Long)
}

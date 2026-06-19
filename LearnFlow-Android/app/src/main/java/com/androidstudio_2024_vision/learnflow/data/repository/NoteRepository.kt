package com.androidstudio_2024_vision.learnflow.data.repository

import android.icu.text.CaseMap.Title
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import com.androidstudio_2024_vision.learnflow.data.room.NoteDao
import com.androidstudio_2024_vision.learnflow.data.room.NoteEntity
import com.androidstudio_2024_vision.learnflow.network.ApiService

class NoteRepository(
    private val api: ApiService
) {

    suspend fun addNote(
        note: NoteDto
    ) {

        api.addNote(note)
    }

    suspend fun getNotes(
        userId: Long
    ): List<NoteDto> {

        return api.getNotes(userId)
    }

    suspend fun deleteNote(
        id: Long
    ) {

        api.deleteNote(id)
    }

    suspend fun getNoteCount(
        userId: Long
    ): Long {

        return api.getNoteCount(userId)
    }
}
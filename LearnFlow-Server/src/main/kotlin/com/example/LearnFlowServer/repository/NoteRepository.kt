package com.example.LearnFlowServer.repository

import com.example.LearnFlowServer.entity.NoteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository :
    JpaRepository<NoteEntity, Long> {

    fun findByUserId(
        userId: Long
    ): List<NoteEntity>

    fun countByUserId(
        userId: Long
    ): Long
}
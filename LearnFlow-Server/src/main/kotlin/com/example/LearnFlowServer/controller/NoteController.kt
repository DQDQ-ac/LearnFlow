package com.example.LearnFlowServer.controller

import com.example.LearnFlowServer.entity.NoteEntity
import com.example.LearnFlowServer.repository.NoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notes")
class NoteController {

    @Autowired
    lateinit var noteRepository: NoteRepository

    @PostMapping("/add")
    fun addNote(
        @RequestBody note: NoteEntity
    ): NoteEntity {

        return noteRepository.save(note)
    }

    @GetMapping("/list/{userId}")
    fun getNotes(
        @PathVariable userId: Long
    ): List<NoteEntity> {

        return noteRepository.findByUserId(userId)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteNote(
        @PathVariable id: Long
    ) {

        noteRepository.deleteById(id)
    }

    @GetMapping("/count/{userId}")
    fun getNoteCount(
        @PathVariable userId: Long
    ): Long {

        return noteRepository.countByUserId(userId)
    }
}
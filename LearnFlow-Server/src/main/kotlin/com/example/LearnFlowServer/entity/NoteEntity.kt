package com.example.LearnFlowServer.entity

import jakarta.persistence.*

@Entity
@Table(name = "notes")
data class NoteEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "video_title")
    val videoTitle: String,

    @Column(name = "note_text")
    val noteText: String,

    val position: Long,

    @Column(name = "image_path")
    val imagePath: String? = null
)
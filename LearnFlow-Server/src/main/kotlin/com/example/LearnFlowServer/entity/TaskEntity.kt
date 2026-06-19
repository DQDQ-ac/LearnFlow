package com.example.LearnFlowServer.entity

import jakarta.persistence.*

@Entity
@Table(name = "task")
class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var taskId: Long = 0

    var userId: Long = 0

    var title: String = ""

    var completed: Boolean = false

    var date: String = ""

    var priority: Int = 1
}
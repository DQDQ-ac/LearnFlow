package com.example.LearnFlowServer.repository

import com.example.LearnFlowServer.entity.Task
import org.springframework.data.jpa.repository.JpaRepository


interface TaskRepository : JpaRepository<Task, Long>{

    fun findByUserId(
        userId: Long
    ): List<Task>
}
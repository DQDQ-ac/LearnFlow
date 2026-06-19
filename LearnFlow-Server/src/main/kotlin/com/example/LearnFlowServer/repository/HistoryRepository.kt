package com.example.LearnFlowServer.repository

import com.example.LearnFlowServer.entity.HistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HistoryRepository :
    JpaRepository<HistoryEntity, Long> {

    fun findByUserId(
        userId: Long
    ): List<HistoryEntity>

    fun findTopByUserIdOrderByUpdateTimeDesc(
        userId: Long
    ): HistoryEntity?

    fun findByUserIdAndVideoTitle(
        userId: Long,
        videoTitle: String
    ): HistoryEntity?
}
package com.example.LearnFlowServer.repository

import com.example.LearnFlowServer.entity.StudyHeatMap
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*


interface StudyHeatMapRepository : JpaRepository<StudyHeatMap, Long> {

    fun findByUserIdAndDay(userId: Long, day: LocalDate): StudyHeatMap?

    fun findByUserIdAndDayBetween(
        userId: Long,
        start: LocalDate,
        end: LocalDate
    ): List<StudyHeatMap>
}
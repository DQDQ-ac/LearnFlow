package com.example.LearnFlowServer.controller

import com.example.LearnFlowServer.dto.HeatMapDto
import com.example.LearnFlowServer.entity.StudyHeatMap
import com.example.LearnFlowServer.repository.StudyHeatMapRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/heatmap")
class HeatMapController(private val repository: StudyHeatMapRepository) {

    // 每次累加今天的学习秒数
    @PostMapping("/addDuration/{userId}/{seconds}")
    fun addDuration(
        @PathVariable userId: Long,
        @PathVariable seconds: Int
    ) {
        val today = LocalDate.now()
        val heatMap = repository.findByUserIdAndDay(userId, today)
            ?: StudyHeatMap().apply {
                this.userId = userId
                this.day = today
                this.totalDuration = 0
            }
        heatMap.totalDuration = (heatMap.totalDuration ?: 0) + seconds
        repository.save(heatMap)
    }

    // 获取最近7天
    @GetMapping("/last7Days/{userId}")
    fun last7Days(@PathVariable userId: Long): List<HeatMapDto> {

        val today = LocalDate.now()
        val start = today.minusDays(6)

        val list = repository.findByUserIdAndDayBetween(userId, start, today)

        return list.map { h ->
            HeatMapDto(
                day = h.day.toString(),
                totalDuration = h.totalDuration ?: 0
            )
        }
    }
}
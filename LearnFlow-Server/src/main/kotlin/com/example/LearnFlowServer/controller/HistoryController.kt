package com.example.LearnFlowServer.controller

import com.example.LearnFlowServer.entity.HistoryEntity
import com.example.LearnFlowServer.repository.HistoryRepository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/history")
class HistoryController(
    private val historyRepository: HistoryRepository
) {

    @PostMapping("/save")
    fun save(
        @RequestBody history: HistoryEntity
    ): HistoryEntity {

        val old =
            historyRepository.findByUserIdAndVideoTitle(
                history.userId,
                history.videoTitle
            )

        return historyRepository.save(

            if (old == null)
                history
            else
                old.copy(
                    position = history.position,
                    updateTime = history.updateTime
                )
        )
    }

    @GetMapping("/latest/{userId}")
    fun latest(
        @PathVariable userId: Long
    ): HistoryEntity?{

        return historyRepository
            .findTopByUserIdOrderByUpdateTimeDesc(userId)
    }

    @GetMapping("/list/{userId}")
    fun list(
        @PathVariable userId: Long
    ): List<HistoryEntity> {

        return historyRepository.findByUserId(userId)
    }

    @DeleteMapping("/delete/{id}")
    fun delete(
        @PathVariable id: Long
    ) {

        historyRepository.deleteById(id)
    }
}
package com.example.LearnFlowServer.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "study_heat_map",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "day"])]
)
class StudyHeatMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null

    @Column(name = "day", nullable = false)
    var day: LocalDate? = null

    @Column(name = "total_duration", nullable = false)
    var totalDuration: Int? = null

}
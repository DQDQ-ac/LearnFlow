package com.example.LearnFlowServer.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(

    @Id
    @GeneratedValue(
        strategy =
            GenerationType.IDENTITY
    )
    val id: Long = 0,

    @Column(
        unique = true,
        nullable = false
    )
    val username: String,

    @Column(
        nullable = false
    )
    val password: String,

    val nickname: String
)
package com.example.LearnFlowServer.repository

import com.example.LearnFlowServer.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository :
    JpaRepository<UserEntity,Long> {

    fun findByUsername(
        username:String
    ): UserEntity?
}
package com.androidstudio_2024_vision.learnflow.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(
        onConflict =
        OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        task: TaskEntity
    )

    @Update
    suspend fun update(
        task: TaskEntity
    )

    @Delete
    suspend fun delete(
        task: TaskEntity
    )

    @Query(
        """
        SELECT * FROM tasks
        WHERE userId=:userId
        ORDER BY taskId DESC
        """
    )
    fun getTasks(
        userId: Long
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun debugAll(): Flow<List<TaskEntity>>
}
package com.androidstudio_2024_vision.learnflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        history:HistoryEntity
    )
//拿最近一条学习记录
    @Query("SELECT * FROM history WHERE userId = :userId ORDER BY updateTime DESC LIMIT 1")
    fun getLatestHistory(userId: Long): Flow<HistoryEntity?>
//累计学习：SUM(position)
    @Query(
        """
    SELECT SUM(position)
    FROM history
    WHERE userId=:userId
    """
    )
    fun getTotalStudyTime(
        userId: Long
    ): Flow<Long?>
//课程数：
    @Query(
        """
    SELECT COUNT(DISTINCT videoTitle)
    FROM history
    WHERE userId=:userId
    """
    )
    fun getCourseCount(
        userId: Long
    ): Flow<Int>
 //学习记录：历史条数
    @Query(
        """
    SELECT COUNT(*)
    FROM history
    WHERE userId=:userId
    """
    )
    fun getHistoryCount(
        userId: Long
    ): Flow<Int>
//统计：学习发生过多少天。
    @Query(
        """
    SELECT COUNT(
        DISTINCT date(
            updateTime/1000,
            'unixepoch'
        )
    )
    FROM history
    WHERE userId=:userId
    """
    )
    fun getStudyDays(
    userId: Long
    ): Flow<Int>

    @Query(
        """
    SELECT
    date(
        updateTime/1000,
        'unixepoch'
    ) as day,

    COUNT(*) as count

    FROM history

    WHERE userId=:userId

    GROUP BY day
    """
    )
    fun getStudyHeatMap(
        userId: Long
    ): Flow<List<StudyHeatMap>>
}
package com.androidstudio_2024_vision.learnflow.data.room

import android.icu.text.CaseMap.Title
import androidx.room.*
import kotlinx.coroutines.flow.Flow
//数据库操作层
@Dao
interface NoteDao {

    @Insert
    suspend fun insert(
        note: NoteEntity
    )

    @Query(
        "SELECT * FROM notes WHERE userId=:userId AND videoTitle=:videoTitle"
    )
    fun getNotes(
        userId: Long,
        videoTitle: String
    ): Flow<List<NoteEntity>>

    //统计重点数量
    @Query(
        """
    SELECT COUNT(*)
    FROM notes
    WHERE userId=:userId
    """
    )
    fun getNoteCount(
        userId: Long
    ): Flow<Int>

    @Delete
    suspend fun delete(
        note: NoteEntity
    )
}
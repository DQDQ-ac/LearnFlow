package com.androidstudio_2024_vision.learnflow.network

import androidx.room.Query
import com.androidstudio_2024_vision.learnflow.data.dto.HeatMapDto
import com.androidstudio_2024_vision.learnflow.data.dto.HistoryDto
import com.androidstudio_2024_vision.learnflow.data.dto.NoteDto
import com.androidstudio_2024_vision.learnflow.data.dto.TaskDto
import com.androidstudio_2024_vision.learnflow.data.room.StudyHeatMap
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("user/login")
    suspend fun login(

        @Body request: LoginRequest

    ): ApiResponse<LoginResponse>

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<Any>


    @POST("/tasks/add")
    suspend fun addTask(@Body task: TaskDto)
    @GET("/tasks/list/{userId}")
    suspend fun getTasks(@Path("userId") userId: Long): List<TaskDto>
    @PUT("/tasks/update")
    suspend fun updateTask(@Body task: TaskDto)
    @DELETE("/tasks/delete/{id}")
    suspend fun deleteTask(@Path("id") id: Long)


    @POST("/notes/add")
    suspend fun addNote(
        @Body note: NoteDto
    )
    @GET("/notes/list/{userId}")
    suspend fun getNotes(
        @Path("userId")
        userId: Long
    ): List<NoteDto>
    @DELETE("/notes/delete/{id}")
    suspend fun deleteNote(
        @Path("id")
        id: Long
    )
    @GET("/notes/count/{userId}")
    suspend fun getNoteCount(
        @Path("userId")
        userId: Long
    ): Long


    @POST("/history/save")
    suspend fun saveHistory(
        @Body history: HistoryDto
    )

    @GET("/history/latest/{userId}")
    suspend fun getLatestHistory(
        @Path("userId") userId: Long
    ): HistoryDto?

    @GET("/history/list/{userId}")
    suspend fun getHistoryList(
        @Path("userId") userId: Long
    ): List<HistoryDto>

    @DELETE("/history/delete/{id}")
    suspend fun deleteHistory(
        @Path("id") id: Long
    )

    @POST("/heatmap/addDuration/{userId}/{seconds}")
    suspend fun addHeatMapDuration(
        @Path("userId") userId: Long,
        @Path("seconds") seconds: Int
    )

    @GET("/heatmap/last7Days/{userId}")
    suspend fun getLast7DaysHeatMap(
        @Path("userId") userId: Long
    ): List<HeatMapDto>

}
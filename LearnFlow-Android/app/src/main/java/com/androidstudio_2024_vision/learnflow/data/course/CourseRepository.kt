package com.androidstudio_2024_vision.learnflow.data.course

import android.content.Context
import android.net.Uri
import com.androidstudio_2024_vision.learnflow.R

class CourseRepository {

    fun getCourses(context: Context): List<Course> {

        return listOf(

            Course(
                id = 1,
                title = "数据结构",
                duration = "6'33",
                videoUrl = "asset:///DataStructure001.mp4"
            ),

            Course(
                id = 2,
                title = "数据结构单链表",
                duration = "26'20",
                videoUrl = "asset:///DS_LinkList.mp4"
            ),

            Course(
                id = 3,
                title = "高等数学",
                duration = "18'03",
                videoUrl = "asset:///AdvancedMathematics.mp4"
            ),

            Course(
                id = 4,
                title = "计算机网络",
                duration = "22'11",
                videoUrl = "asset:///ComputerNetwork.mp4"
            )
        )
    }
}
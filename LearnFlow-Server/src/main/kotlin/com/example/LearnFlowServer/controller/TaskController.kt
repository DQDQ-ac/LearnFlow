package com.example.LearnFlowServer.controller

import com.example.LearnFlowServer.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.LearnFlowServer.dto.TaskDTO
import com.example.LearnFlowServer.entity.Task
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/tasks")
class TaskController {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @PostMapping("/add")
    fun addTask(@RequestBody dto: TaskDTO): Task {

        val task = Task()

        task.userId = dto.userId ?: 0
        task.title = dto.title ?: ""
        task.completed = dto.completed ?: false
        task.priority = dto.priority ?: 1
        task.date = dto.date ?: ""

        return taskRepository.save(task)
    }

    // =====================
    // 2. 查询用户任务
    // =====================
    @GetMapping("/list/{userId}")
    fun getTasks(@PathVariable userId: Long): List<Task> {
        return taskRepository.findByUserId(userId)
    }

    // =====================
    // 3. 更新任务（完成/未完成）
    // =====================
    @PutMapping("/update")
    fun updateTask(@RequestBody task: Task): Task {
        return taskRepository.save(task)
    }

    // =====================
    // 4. 删除任务
    // =====================
    @DeleteMapping("/delete/{id}")
    fun deleteTask(@PathVariable id: Long) {
        taskRepository.deleteById(id)
    }
}



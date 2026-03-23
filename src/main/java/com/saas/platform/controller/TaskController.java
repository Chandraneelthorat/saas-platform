package com.saas.platform.controller;

import com.saas.platform.dto.TaskRequest;
import com.saas.platform.dto.TaskResponse;
import com.saas.platform.dto.TaskSummary;
import com.saas.platform.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // POST /api/projects/{projectId}/tasks
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request));
    }

    // GET /api/projects/{projectId}/tasks
    @GetMapping
    public ResponseEntity<List<TaskSummary>> getAllTasks(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getAllTasksForProject(projectId));
    }

    // GET /api/projects/{projectId}/tasks/{taskId}
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(projectId, taskId));
    }

    // PUT /api/projects/{projectId}/tasks/{taskId}
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, request));
    }

    // DELETE /api/projects/{projectId}/tasks/{taskId}
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}

package com.saas.platform.service;

import com.saas.platform.dto.TaskRequest;
import com.saas.platform.dto.TaskResponse;
import com.saas.platform.dto.TaskSummary;
import com.saas.platform.model.Project;
import com.saas.platform.model.Task;
import com.saas.platform.model.User;
import com.saas.platform.repository.ProjectRepository;
import com.saas.platform.repository.TaskRepository;
import com.saas.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ── Helper: get logged-in user from JWT ──────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Helper: get project scoped to tenant ─────────────────
    private Project getProjectForCurrentTenant(Long projectId) {
        User currentUser = getCurrentUser();
        return projectRepository
                .findByIdAndTenantId(projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    // ── Create a new task ────────────────────────────────────
    public TaskResponse createTask(Long projectId, TaskRequest request) {
        User currentUser = getCurrentUser();
        Project project = getProjectForCurrentTenant(projectId);

        // Resolve assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .project(project)
                .assignee(assignee)
                .createdBy(currentUser)
                .dueDate(request.getDueDate())
                .build();

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    // ── Get all tasks for a project ──────────────────────────
    public List<TaskSummary> getAllTasksForProject(Long projectId) {
        User currentUser = getCurrentUser();
        return taskRepository
                .findByProjectIdAndProjectTenantId(
                        projectId, currentUser.getTenant().getId())
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ── Get a single task by ID ──────────────────────────────
    public TaskResponse getTaskById(Long projectId, Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository
                .findByIdAndProjectIdAndProjectTenantId(
                        taskId, projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToResponse(task);
    }

    // ── Update a task ────────────────────────────────────────
    public TaskResponse updateTask(Long projectId, Long taskId, TaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository
                .findByIdAndProjectIdAndProjectTenantId(
                        taskId, projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Resolve assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setAssignee(assignee);
        task.setDueDate(request.getDueDate());

        Task updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    // ── Delete a task ────────────────────────────────────────
    public void deleteTask(Long projectId, Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository
                .findByIdAndProjectIdAndProjectTenantId(
                        taskId, projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    // ── Map entity → full response DTO ───────────────────────
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .assigneeId(task.getAssignee() != null ?
                        task.getAssignee().getId() : null)
                .assigneeEmail(task.getAssignee() != null ?
                        task.getAssignee().getEmail() : null)
                .createdByEmail(task.getCreatedBy().getEmail())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    // ── Map entity → summary DTO ─────────────────────────────
    private TaskSummary mapToSummary(Task task) {
        return TaskSummary.builder()
                .id(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeEmail(task.getAssignee() != null ?
                        task.getAssignee().getEmail() : null)
                .dueDate(task.getDueDate())
                .build();
    }
}
package com.saas.platform.dto;

import com.saas.platform.model.Task.TaskPriority;
import com.saas.platform.model.Task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeEmail;
    private String createdByEmail;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
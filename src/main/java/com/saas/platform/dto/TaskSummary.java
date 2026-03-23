package com.saas.platform.dto;

import com.saas.platform.model.Task.TaskPriority;
import com.saas.platform.model.Task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskSummary {

    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private String assigneeEmail;
    private LocalDateTime dueDate;
}

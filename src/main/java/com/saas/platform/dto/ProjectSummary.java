package com.saas.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectSummary {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
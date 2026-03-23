package com.saas.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long tenantId;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
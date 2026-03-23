package com.saas.platform.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.saas.platform.dto.ProjectRequest;
import com.saas.platform.dto.ProjectResponse;
import com.saas.platform.dto.ProjectSummary;
import com.saas.platform.model.Project;
import com.saas.platform.model.User;
import com.saas.platform.repository.ProjectRepository;
import com.saas.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ── Helper: get the logged-in user from JWT ──────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Create a new project ─────────────────────────────────
    public ProjectResponse createProject(ProjectRequest request) {
        User currentUser = getCurrentUser();

        if (projectRepository.existsByNameAndTenantId(
                request.getName(), currentUser.getTenant().getId())) {
            throw new RuntimeException("Project with this name already exists");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .tenant(currentUser.getTenant())
                .createdBy(currentUser)
                .build();

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    // ── Get all projects for current user's tenant ───────────
    @Cacheable(value = "projects", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")    public List<ProjectSummary> getAllProjects() {
        User currentUser = getCurrentUser();
        return projectRepository
                .findByTenantId(currentUser.getTenant().getId())
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    // ── Get a single project by ID ───────────────────────────
    public ProjectResponse getProjectById(Long projectId) {
        User currentUser = getCurrentUser();
        Project project = projectRepository
                .findByIdAndTenantId(projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToResponse(project);
    }

    // ── Update a project ─────────────────────────────────────
    @CacheEvict(value = "projects", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        User currentUser = getCurrentUser();
        Project project = projectRepository
                .findByIdAndTenantId(projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updated = projectRepository.save(project);
        return mapToResponse(updated);
    }

    // ── Delete a project ─────────────────────────────────────
    @CacheEvict(value = "projects", key = "#root.authentication.name")
    public void deleteProject(Long projectId) {
        User currentUser = getCurrentUser();
        Project project = projectRepository
                .findByIdAndTenantId(projectId, currentUser.getTenant().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    // ── Map entity → full response DTO ───────────────────────
    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .tenantId(project.getTenant().getId())
                .createdByEmail(project.getCreatedBy().getEmail())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    // ── Map entity → summary DTO ─────────────────────────────
    private ProjectSummary mapToSummary(Project project) {
        return ProjectSummary.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
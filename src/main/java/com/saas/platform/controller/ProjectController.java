package com.saas.platform.controller;

import com.saas.platform.dto.ProjectRequest;
import com.saas.platform.dto.ProjectResponse;
import com.saas.platform.dto.ProjectSummary;
import com.saas.platform.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // POST /api/projects
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request));
    }

    // GET /api/projects
    @GetMapping
    public ResponseEntity<List<ProjectSummary>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // GET /api/projects/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // PUT /api/projects/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    // DELETE /api/projects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
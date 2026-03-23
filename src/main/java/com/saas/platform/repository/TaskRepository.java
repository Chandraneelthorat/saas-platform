package com.saas.platform.repository;

import com.saas.platform.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Get all tasks belonging to a specific project
    List<Task> findByProjectId(Long projectId);

    // Get all tasks belonging to a specific project AND tenant (safe query)
    List<Task> findByProjectIdAndProjectTenantId(Long projectId, Long tenantId);

    // Get a specific task only if it belongs to the correct project and tenant
    Optional<Task> findByIdAndProjectIdAndProjectTenantId(Long id, Long projectId, Long tenantId);

    // Get all tasks assigned to a specific user within a tenant
    List<Task> findByAssigneeIdAndProjectTenantId(Long assigneeId, Long tenantId);
}
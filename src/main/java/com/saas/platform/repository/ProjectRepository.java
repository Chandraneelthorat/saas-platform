package com.saas.platform.repository;

import com.saas.platform.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Get all projects belonging to a specific tenant
    List<Project> findByTenantId(Long tenantId);

    // Get a specific project only if it belongs to the tenant
    Optional<Project> findByIdAndTenantId(Long id, Long tenantId);

    // Check if a project name already exists in this tenant
    boolean existsByNameAndTenantId(String name, Long tenantId);
}
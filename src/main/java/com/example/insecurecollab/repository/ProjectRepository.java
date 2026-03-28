package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByWorkspaceIdOrderByCreatedAtDesc(Long workspaceId);
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    List<Project> findByPublishedTrueOrderByCreatedAtDesc();
}

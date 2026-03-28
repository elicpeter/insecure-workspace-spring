package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findBySlug(String slug);
}

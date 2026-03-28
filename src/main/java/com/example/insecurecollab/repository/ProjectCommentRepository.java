package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
    List<ProjectComment> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}

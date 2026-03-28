package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}

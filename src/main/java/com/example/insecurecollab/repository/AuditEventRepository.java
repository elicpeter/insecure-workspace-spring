package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findTop20ByWorkspaceIdOrderByCreatedAtDesc(Long workspaceId);
    List<AuditEvent> findTop50ByOrderByCreatedAtDesc();
}

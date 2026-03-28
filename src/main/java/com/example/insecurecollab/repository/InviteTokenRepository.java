package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {
    Optional<InviteToken> findByToken(String token);
    List<InviteToken> findByWorkspaceId(Long workspaceId);
}

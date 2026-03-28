package com.example.insecurecollab.repository;

import com.example.insecurecollab.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserId(Long userId);
    List<Membership> findByWorkspaceId(Long workspaceId);
    Optional<Membership> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);
}

package com.example.insecurecollab.service;

import com.example.insecurecollab.model.InviteToken;
import com.example.insecurecollab.model.Membership;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.model.Workspace;
import com.example.insecurecollab.repository.InviteTokenRepository;
import com.example.insecurecollab.repository.MembershipRepository;
import com.example.insecurecollab.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MembershipRepository membershipRepository;
    private final InviteTokenRepository inviteTokenRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            MembershipRepository membershipRepository,
                            InviteTokenRepository inviteTokenRepository) {
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
        this.inviteTokenRepository = inviteTokenRepository;
    }

    public List<Workspace> allWorkspaces() {
        return workspaceRepository.findAll();
    }

    public Workspace getWorkspace(Long id) {
        return workspaceRepository.findById(id).orElseThrow();
    }

    public boolean isMember(Long userId, Long workspaceId) {
        return membershipRepository.findByUserIdAndWorkspaceId(userId, workspaceId).isPresent();
    }

    public List<Membership> membershipsForUser(Long userId) {
        return membershipRepository.findByUserId(userId);
    }

    public Workspace createWorkspace(String name, String slug, String visibility, UserAccount owner) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setSlug(slug);
        workspace.setVisibility(visibility);
        workspace.setOwner(owner);
        Workspace saved = workspaceRepository.save(workspace);

        Membership membership = new Membership();
        membership.setUser(owner);
        membership.setWorkspace(saved);
        membership.setRole("OWNER");
        membershipRepository.save(membership);
        return saved;
    }

    public Membership joinWorkspace(String tokenValue, UserAccount user) {
        InviteToken inviteToken = inviteTokenRepository.findByToken(tokenValue).orElseThrow();
        if (inviteToken.getExpiresAt() != null && inviteToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invite expired");
        }

        Membership membership = membershipRepository.findByUserIdAndWorkspaceId(user.getId(), inviteToken.getWorkspace().getId())
                .orElseGet(Membership::new);
        membership.setUser(user);
        membership.setWorkspace(inviteToken.getWorkspace());
        membership.setRole(inviteToken.getInvitedRole());
        inviteToken.setClaimedByUser(user);
        inviteTokenRepository.save(inviteToken);
        return membershipRepository.save(membership);
    }

    public InviteToken createInvite(Long workspaceId, String emailHint, String role) {
        InviteToken token = new InviteToken();
        token.setWorkspace(getWorkspace(workspaceId));
        token.setEmailHint(emailHint);
        token.setInvitedRole(role);
        token.setToken("invite-" + workspaceId + "-" + System.currentTimeMillis());
        token.setExpiresAt(LocalDateTime.now().plusDays(14));
        return inviteTokenRepository.save(token);
    }
}

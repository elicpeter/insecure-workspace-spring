package com.example.insecurecollab.service;

import com.example.insecurecollab.model.AuditEvent;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.repository.AuditEventRepository;
import com.example.insecurecollab.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final AuditEventRepository auditEventRepository;

    public AdminService(UserAccountRepository userAccountRepository, AuditEventRepository auditEventRepository) {
        this.userAccountRepository = userAccountRepository;
        this.auditEventRepository = auditEventRepository;
    }

    @Value("${app.fake-support-token}")
    private String fakeSupportToken;

    @Value("${app.fake-webhook-secret}")
    private String fakeWebhookSecret;

    public List<UserAccount> allUsers() {
        return userAccountRepository.findAll();
    }

    public List<AuditEvent> recentEvents() {
        return auditEventRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> debugSnapshot() {
        return Map.of(
                "supportToken", fakeSupportToken,
                "webhookSecret", fakeWebhookSecret,
                "userCount", userAccountRepository.count(),
                "events", auditEventRepository.findTop50ByOrderByCreatedAtDesc()
        );
    }
}

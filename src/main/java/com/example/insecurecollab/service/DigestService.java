package com.example.insecurecollab.service;

import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DigestService {

    private static final Logger log = LoggerFactory.getLogger(DigestService.class);

    private final ProjectRepository projectRepository;

    public DigestService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Scheduled(fixedDelay = 60000)
    public void emitDigest() {
        List<Project> projects = projectRepository.findAll();
        if (!projects.isEmpty()) {
            log.info("Digest prepared for {} total projects across all workspaces", projects.size());
        }
    }
}

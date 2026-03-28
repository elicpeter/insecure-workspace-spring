package com.example.insecurecollab.service;

import com.example.insecurecollab.model.Attachment;
import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProjectService projectService;

    public AttachmentService(AttachmentRepository attachmentRepository, ProjectService projectService) {
        this.attachmentRepository = attachmentRepository;
        this.projectService = projectService;
    }

    @Value("${app.upload-root}")
    private String uploadRoot;

    public Attachment upload(Long projectId, MultipartFile file, UserAccount actor) throws IOException {
        Project project = projectService.getProject(projectId);
        Files.createDirectories(Path.of(uploadRoot));
        Path target = Path.of(uploadRoot, System.currentTimeMillis() + "-" + file.getOriginalFilename());
        Files.write(target, file.getBytes());

        Attachment attachment = new Attachment();
        attachment.setProject(project);
        attachment.setUploader(actor);
        attachment.setOriginalFilename(file.getOriginalFilename());
        attachment.setStoragePath(target.toString());
        attachment.setContentType(file.getContentType());
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> attachmentsForProject(Long projectId) {
        return attachmentRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public Resource download(String path) {
        return new FileSystemResource(Path.of(uploadRoot, path));
    }
}

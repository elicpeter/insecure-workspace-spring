package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.service.AttachmentService;
import com.example.insecurecollab.service.AuthService;
import com.example.insecurecollab.service.ExportService;
import com.example.insecurecollab.service.LinkPreviewService;
import com.example.insecurecollab.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AuthService authService;
    private final ProjectService projectService;
    private final AttachmentService attachmentService;
    private final LinkPreviewService linkPreviewService;
    private final ExportService exportService;

    public ApiController(AuthService authService,
                         ProjectService projectService,
                         AttachmentService attachmentService,
                         LinkPreviewService linkPreviewService,
                         ExportService exportService) {
        this.authService = authService;
        this.projectService = projectService;
        this.attachmentService = attachmentService;
        this.linkPreviewService = linkPreviewService;
        this.exportService = exportService;
    }

    @GetMapping("/projects/{id}")
    public Map<String, Object> project(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        return Map.of(
                "id", project.getId(),
                "name", project.getName(),
                "description", project.getDescription(),
                "status", project.getStatus(),
                "workspaceId", project.getWorkspace().getId(),
                "workspaceName", project.getWorkspace().getName(),
                "ownerId", project.getOwner().getId(),
                "privateProject", project.isPrivateProject(),
                "published", project.isPublished()
        );
    }

    @GetMapping("/projects/{id}/comments")
    public Object comments(@PathVariable Long id) {
        return projectService.commentsForProject(id).stream()
                .map(comment -> Map.of(
                        "id", comment.getId(),
                        "authorId", comment.getAuthor().getId(),
                        "authorName", comment.getAuthor().getFullName(),
                        "body", comment.getBody(),
                        "internalNote", comment.isInternalNote()
                ))
                .toList();
    }

    @PostMapping("/projects/{id}/status")
    public Project updateStatus(@PathVariable Long id,
                                @RequestParam String status,
                                @RequestParam(defaultValue = "false") boolean published) {
        Project project = projectService.getProject(id);
        return projectService.updateProject(id, project.getName(), project.getDescription(), status, published);
    }

    @PostMapping("/projects/bulk-close")
    public Map<String, Object> bulkClose(@RequestBody List<Long> ids, HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (!ids.isEmpty()) {
            Project first = projectService.getProject(ids.get(0));
            if (currentUser != null && first.getOwner().getId().equals(currentUser.getId())) {
                for (Long id : ids) {
                    Project project = projectService.getProject(id);
                    projectService.updateProject(project.getId(), project.getName(), project.getDescription(), "CLOSED", project.isPublished());
                }
            }
        }
        return Map.of("closed", ids.size());
    }

    @GetMapping("/search")
    public Object search(@RequestParam String q) {
        return projectService.insecureSearch(q);
    }

    @GetMapping("/attachments/download")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        Resource resource = attachmentService.download(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/preview")
    public Map<String, Object> preview(@RequestParam String url) {
        return Map.of("preview", linkPreviewService.fetch(url));
    }

    @PostMapping("/exports/workspaces/{workspaceId}")
    public Map<String, Object> export(@PathVariable Long workspaceId,
                                      @RequestParam(defaultValue = "report") String fileName) throws IOException {
        return Map.of("archive", exportService.exportWorkspace(workspaceId, fileName));
    }
}

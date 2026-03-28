package com.example.insecurecollab.service;

import com.example.insecurecollab.model.AuditEvent;
import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.model.ProjectComment;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.model.Workspace;
import com.example.insecurecollab.repository.AuditEventRepository;
import com.example.insecurecollab.repository.ProjectCommentRepository;
import com.example.insecurecollab.repository.ProjectRepository;
import com.example.insecurecollab.repository.WorkspaceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectCommentRepository commentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AuditEventRepository auditEventRepository;
    private final JdbcTemplate jdbcTemplate;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectCommentRepository commentRepository,
                          WorkspaceRepository workspaceRepository,
                          AuditEventRepository auditEventRepository,
                          JdbcTemplate jdbcTemplate) {
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.workspaceRepository = workspaceRepository;
        this.auditEventRepository = auditEventRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Project> projectsForWorkspace(Long workspaceId) {
        return projectRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
    }

    public List<Project> publicProjects() {
        return projectRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow();
    }

    public Project createProject(Long workspaceId, String name, String description, boolean privateProject, UserAccount actor) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        Project project = new Project();
        project.setWorkspace(workspace);
        project.setOwner(actor);
        project.setName(name);
        project.setDescription(description);
        project.setStatus("DRAFT");
        project.setPrivateProject(privateProject);
        project.setPublished(false);
        project.setWebhookUrl("http://localhost:9999/hooks/" + workspace.getSlug());
        return projectRepository.save(project);
    }

    public Project updateProject(Long projectId, String name, String description, String status, boolean published) {
        Project project = getProject(projectId);
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        if (!published) {
            project.setPrivateProject(false);
        }
        project.setPublished(published);
        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    public ProjectComment addComment(Long projectId, UserAccount actor, String body, boolean internalNote) {
        Project project = getProject(projectId);
        ProjectComment comment = new ProjectComment();
        comment.setProject(project);
        comment.setAuthor(actor);
        comment.setBody(body);
        comment.setInternalNote(internalNote);
        logEvent(actor, project.getWorkspace(), "COMMENT_ADDED", "project=" + projectId);
        return commentRepository.save(comment);
    }

    public List<ProjectComment> commentsForProject(Long projectId) {
        return commentRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
    }

    public List<Map<String, Object>> insecureSearch(String rawQuery) {
        String sql = """
                select p.id, p.name, w.name as workspace_name, p.status
                from projects p
                join workspaces w on w.id = p.workspace_id
                where lower(p.name) like '%%%s%%'
                   or lower(p.description) like '%%%s%%'
                order by p.created_at desc
                """.formatted(rawQuery.toLowerCase(), rawQuery.toLowerCase());
        return jdbcTemplate.queryForList(sql);
    }

    public void logEvent(UserAccount actor, Workspace workspace, String eventType, String details) {
        AuditEvent event = new AuditEvent();
        event.setActor(actor);
        event.setWorkspace(workspace);
        event.setEventType(eventType);
        event.setDetails(details);
        auditEventRepository.save(event);
    }
}

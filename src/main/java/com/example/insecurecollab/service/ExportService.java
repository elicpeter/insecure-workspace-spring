package com.example.insecurecollab.service;

import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.model.Workspace;
import com.example.insecurecollab.repository.ProjectRepository;
import com.example.insecurecollab.repository.WorkspaceRepository;
import com.example.insecurecollab.util.ShellReportRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExportService {

    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final ShellReportRunner shellReportRunner;

    public ExportService(WorkspaceRepository workspaceRepository,
                         ProjectRepository projectRepository,
                         ShellReportRunner shellReportRunner) {
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
        this.shellReportRunner = shellReportRunner;
    }

    @Value("${app.export-root}")
    private String exportRoot;

    public String exportWorkspace(Long workspaceId, String requestedName) throws IOException {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        List<Project> projects = projectRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
        Files.createDirectories(Path.of(exportRoot));
        Path exportFile = Path.of(exportRoot, "workspace-" + workspaceId + ".txt");
        StringBuilder builder = new StringBuilder();
        builder.append("Workspace: ").append(workspace.getName()).append("\n");
        for (Project project : projects) {
            builder.append(project.getId()).append(" | ")
                    .append(project.getName()).append(" | ")
                    .append(project.getDescription()).append(" | ")
                    .append(project.getStatus()).append("\n");
        }
        Files.writeString(exportFile, builder.toString());
        return shellReportRunner.buildArchive(exportRoot, workspaceId, requestedName);
    }
}

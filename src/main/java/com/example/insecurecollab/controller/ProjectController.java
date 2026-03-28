package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.Project;
import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.service.AttachmentService;
import com.example.insecurecollab.service.AuthService;
import com.example.insecurecollab.service.ProjectService;
import com.example.insecurecollab.service.WorkspaceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProjectController {

    private final AuthService authService;
    private final WorkspaceService workspaceService;
    private final ProjectService projectService;
    private final AttachmentService attachmentService;

    public ProjectController(AuthService authService,
                             WorkspaceService workspaceService,
                             ProjectService projectService,
                             AttachmentService attachmentService) {
        this.authService = authService;
        this.workspaceService = workspaceService;
        this.projectService = projectService;
        this.attachmentService = attachmentService;
    }

    @GetMapping("/projects/{id}")
    public String project(@PathVariable Long id, HttpServletRequest request, Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        Project project = projectService.getProject(id);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (project.isPrivateProject() && !workspaceService.isMember(currentUser.getId(), project.getWorkspace().getId())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("project", project);
        model.addAttribute("comments", projectService.commentsForProject(id));
        model.addAttribute("attachments", attachmentService.attachmentsForProject(id));
        return "project/detail";
    }

    @PostMapping("/projects")
    public String createProject(@RequestParam Long workspaceId,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam(defaultValue = "true") boolean privateProject,
                                HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        Project project = projectService.createProject(workspaceId, name, description, privateProject, currentUser);
        return "redirect:/projects/" + project.getId();
    }

    @PostMapping("/projects/{id}")
    public String updateProject(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String status,
                                @RequestParam(defaultValue = "false") boolean published) {
        projectService.updateProject(id, name, description, status, published);
        return "redirect:/projects/" + id;
    }

    @PostMapping("/projects/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String body,
                             @RequestParam(defaultValue = "false") boolean internalNote,
                             HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        projectService.addComment(id, currentUser, body, internalNote);
        return "redirect:/projects/" + id;
    }

    @PostMapping("/projects/{id}/attachments")
    public String upload(@PathVariable Long id,
                         @RequestParam("file") MultipartFile file,
                         HttpServletRequest request) throws Exception {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        attachmentService.upload(id, file, currentUser);
        return "redirect:/projects/" + id;
    }

    @PostMapping("/projects/{id}/delete")
    public String delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/dashboard";
    }
}

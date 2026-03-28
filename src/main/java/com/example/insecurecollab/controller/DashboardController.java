package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.Membership;
import com.example.insecurecollab.model.UserAccount;
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

import java.util.List;

@Controller
public class DashboardController {

    private final AuthService authService;
    private final WorkspaceService workspaceService;
    private final ProjectService projectService;

    public DashboardController(AuthService authService, WorkspaceService workspaceService, ProjectService projectService) {
        this.authService = authService;
        this.workspaceService = workspaceService;
        this.projectService = projectService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        List<Membership> memberships = workspaceService.membershipsForUser(currentUser.getId());
        model.addAttribute("memberships", memberships);
        model.addAttribute("publicProjects", projectService.publicProjects());
        return "dashboard";
    }

    @GetMapping("/workspaces/{id}")
    public String workspace(@PathVariable Long id, HttpServletRequest request, Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!workspaceService.isMember(currentUser.getId(), id)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("workspace", workspaceService.getWorkspace(id));
        model.addAttribute("projects", projectService.projectsForWorkspace(id));
        return "workspace";
    }

    @PostMapping("/workspaces")
    public String createWorkspace(@RequestParam String name,
                                  @RequestParam String slug,
                                  @RequestParam(defaultValue = "PRIVATE") String visibility,
                                  HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        workspaceService.createWorkspace(name, slug, visibility, currentUser);
        return "redirect:/dashboard";
    }

    @PostMapping("/workspaces/{id}/invites")
    public String createInvite(@PathVariable Long id,
                               @RequestParam String emailHint,
                               @RequestParam(defaultValue = "MEMBER") String role) {
        workspaceService.createInvite(id, emailHint, role);
        return "redirect:/workspaces/" + id;
    }
}

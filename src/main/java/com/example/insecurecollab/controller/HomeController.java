package com.example.insecurecollab.controller;

import com.example.insecurecollab.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ProjectService projectService;

    public HomeController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("projects", projectService.publicProjects());
        model.addAttribute("q", q == null ? "" : q);
        if (q != null && !q.isBlank()) {
            model.addAttribute("searchResults", projectService.insecureSearch(q));
        }
        return "index";
    }
}

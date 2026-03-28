package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.repository.UserAccountRepository;
import com.example.insecurecollab.service.AuthService;
import com.example.insecurecollab.util.InsecureTemplateRenderer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SettingsController {

    private final AuthService authService;
    private final UserAccountRepository userAccountRepository;
    private final InsecureTemplateRenderer templateRenderer;

    public SettingsController(AuthService authService,
                              UserAccountRepository userAccountRepository,
                              InsecureTemplateRenderer templateRenderer) {
        this.authService = authService;
        this.userAccountRepository = userAccountRepository;
        this.templateRenderer = templateRenderer;
    }

    @GetMapping("/settings")
    public String settings(HttpServletRequest request, Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("preview", "");
        return "settings/index";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String bio,
                                HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        currentUser.setFullName(fullName);
        currentUser.setBio(bio);
        userAccountRepository.save(currentUser);
        return "redirect:/settings";
    }

    @PostMapping("/settings/signature-preview")
    public String signaturePreview(@RequestParam String template,
                                   HttpServletRequest request,
                                   Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        model.addAttribute("preview", templateRenderer.renderUserSuppliedTemplate(template, Map.of("user", currentUser)));
        return "settings/index";
    }
}

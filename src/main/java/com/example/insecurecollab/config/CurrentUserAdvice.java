package com.example.insecurecollab.config;

import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    private final AuthService authService;

    public CurrentUserAdvice(AuthService authService) {
        this.authService = authService;
    }

    @ModelAttribute("currentUser")
    public UserAccount currentUser(HttpServletRequest request) {
        return authService.getCurrentUser(request.getSession(false));
    }

    @ModelAttribute("warningBanner")
    public String warningBanner() {
        return "WARNING: intentionally insecure, AI-generated, local-only demo. Never deploy.";
    }
}

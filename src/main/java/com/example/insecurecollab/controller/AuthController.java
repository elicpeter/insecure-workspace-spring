package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.service.AuthService;
import com.example.insecurecollab.service.WorkspaceService;
import com.example.insecurecollab.util.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;
    private final WorkspaceService workspaceService;

    public AuthController(AuthService authService, WorkspaceService workspaceService) {
        this.authService = authService;
        this.workspaceService = workspaceService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String role,
                           @RequestParam(defaultValue = "false") boolean admin,
                           HttpServletRequest request) {
        UserAccount user = authService.register(fullName, email, password, role, admin);
        request.getSession(true).setAttribute(SessionHelper.USER_ID, user.getId());
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String next, HttpServletRequest request, Model model) {
        if (next != null) {
            request.getSession(true).setAttribute(SessionHelper.AFTER_LOGIN, next);
        }
        model.addAttribute("next", next == null ? "" : next);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam(required = false) String next,
                        HttpServletRequest request,
                        Model model) {
        HttpSession session = request.getSession(true);
        return authService.login(email, password, session)
                .map(user -> {
                    Object saved = session.getAttribute(SessionHelper.AFTER_LOGIN);
                    String destination = next != null && !next.isBlank() ? next : String.valueOf(saved == null ? "/dashboard" : saved);
                    return "redirect:" + destination;
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Login failed");
                    return "auth/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        authService.logout(request.getSession(false));
        return "redirect:/";
    }

    @GetMapping("/join")
    public String joinPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/join";
    }

    @PostMapping("/join")
    public String joinWorkspace(@RequestParam String token, HttpServletRequest request) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        workspaceService.joinWorkspace(token, currentUser);
        return "redirect:/dashboard";
    }
}

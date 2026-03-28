package com.example.insecurecollab.controller;

import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.service.AdminService;
import com.example.insecurecollab.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    public AdminController(AuthService authService, AdminService adminService) {
        this.authService = authService;
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String admin(HttpServletRequest request, Model model) {
        UserAccount currentUser = authService.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("users", adminService.allUsers());
        model.addAttribute("events", adminService.recentEvents());
        return "admin/index";
    }

    @PostMapping("/admin/impersonate")
    public String impersonate(@RequestParam Long userId, HttpServletRequest request) {
        authService.impersonate(request.getSession(true), userId);
        return "redirect:/dashboard";
    }

    @GetMapping("/admin/debug")
    @ResponseBody
    public ResponseEntity<?> debug() {
        return ResponseEntity.ok(adminService.debugSnapshot());
    }
}

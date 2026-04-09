package org.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/etudiant/bulletin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/admin")
    public String loginAdmin() {
        return "login-admin";
    }

    @GetMapping("/login/etudiant")
    public String loginEtudiant() {
        return "login-student";
    }
}

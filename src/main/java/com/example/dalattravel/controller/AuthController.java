package com.example.dalattravel.controller;

import com.example.dalattravel.model.User;
import com.example.dalattravel.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if ("please_login".equals(error)) {
            model.addAttribute("errorMessage", "Vui lòng đăng nhập để truy cập trang quản trị!");
        } else if ("forbidden".equals(error)) {
            model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang Admin!");
        } else if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "Tài khoản hoặc mật khẩu không chính xác!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đã đăng xuất thành công!");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<User> userOpt = authService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Chào mừng " + user.getFullName() + " đã quay trở lại!");

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            }
            return "redirect:/";
        }

        return "redirect:/login?error=invalid";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            authService.registerUser(username, email, password, fullName, phoneNumber, "USER");
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("fullName", fullName);
            model.addAttribute("phoneNumber", phoneNumber);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }
}

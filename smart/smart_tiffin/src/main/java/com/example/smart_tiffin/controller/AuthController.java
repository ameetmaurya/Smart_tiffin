package com.example.smart_tiffin.controller;


import com.example.smart_tiffin.Service.EmailService;
import com.example.smart_tiffin.model.User;
import com.example.smart_tiffin.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private EmailService emailService;



    @GetMapping("/")
    public String showLogin() {
        return "landing"; // Loads login.html
    }

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Loads register.html
    }

    // Handle Registration Form
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {

        userRepository.save(user);
        return "redirect:/"; // Go to login after success
    }

    // Handle Login Form
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // LOGIN SUCCESS: Save user in session
            session.setAttribute("currentUser", user);

            if("DELIVERY".equals(user.getRole())){
                return "redirect:/delivery/dashboard";
            }
            if("ADMIN".equals(user.getRole())){
                return "redirect:/admin/dashboard";
            }
            // Redirect based on role
            if ("COOK".equals(user.getRole())) {
                return "redirect:/cook/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        } else {
            // LOGIN FAILED
            model.addAttribute("error", "Invalid Email or Password");
            return "login";
        }
    }

    // Handle Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }




    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "We could not find an account with that email.");
            return "forgot-password";
        }

        // Generate Token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // Create Link
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;

        // --- UPDATED EMAIL BODY (HTML) ---
        // This will show "reset-password" as a clickable link
        String emailBody = "<h3>Password Reset Request</h3>" +
                "<p>You requested to reset your password. Click the link below:</p>" +
                "<p><a href='" + resetUrl + "' style='background-color: #ea580c; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>reset-password</a></p>" +
                "<p>If you didn't ask for this, ignore this email.</p>";

        emailService.sendEmail(email, "Reset Your Smart Tiffin Password", emailBody);

        model.addAttribute("message", "We have sent a reset link to your email.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, Model model) {
        User user = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getResetToken()))
                .findFirst()
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "Invalid or expired token.");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        User user = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getResetToken()))
                .findFirst()
                .orElse(null);

        if (user == null) return "redirect:/login";

        user.setPassword(password);
        user.setResetToken(null);
        userRepository.save(user);

        return "redirect:/login?message=Password+changed+successfully";
    }



}
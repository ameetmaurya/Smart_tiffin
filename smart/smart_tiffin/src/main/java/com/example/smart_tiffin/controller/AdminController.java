package com.example.smart_tiffin.controller;



import com.example.smart_tiffin.model.User;
import com.example.smart_tiffin.repository.OrderRepository;
import com.example.smart_tiffin.repository.PlanRepository;
import com.example.smart_tiffin.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private PlanRepository planRepository;
    @Autowired private OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/";
        }

        // 1. Fetch Stats
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long totalPlans = planRepository.count();

        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(order -> order.getPlan().getPrice())
                .sum();

        List<User> allUsers = userRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("stats", new long[]{totalUsers, totalOrders, totalPlans});
        model.addAttribute("revenue", totalRevenue);
        model.addAttribute("allUsers", allUsers);

        return "admin-dashboard";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Cannot delete user with active orders.");
        }
        return "redirect:/admin/dashboard";
    }
}
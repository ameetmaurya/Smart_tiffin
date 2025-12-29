package com.example.smart_tiffin.controller;

import com.example.smart_tiffin.model.Order;
import com.example.smart_tiffin.model.TiffinPlan;
import com.example.smart_tiffin.model.User;
import com.example.smart_tiffin.repository.OrderRepository;
import com.example.smart_tiffin.repository.PlanRepository;
import com.example.smart_tiffin.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findById(user.getId()).orElse(user);

        List<TiffinPlan> allPlans = planRepository.findAll();
        List<Order> recentOrders = orderRepository.findTop5ByStudentIdOrderByIdDesc(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("plans", allPlans);
        model.addAttribute("orders", recentOrders);

        return "student-dashboard";
    }

    @GetMapping("/order/{planId}")
    public String placeOrder(@PathVariable Long planId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        TiffinPlan plan = planRepository.findById(planId).orElse(null);
        User student = userRepository.findById(user.getId()).orElse(null);

        if (plan != null && student != null) {
            // FIX: Handle null balance by treating it as 0.0
            Double currentBalance = (student.getWalletBalance() != null) ? student.getWalletBalance() : 0.0;

            // INSUFFICIENT FUNDS CHECK
            if (currentBalance < plan.getPrice()) {
                return "redirect:/student/dashboard?error=insufficient_funds";
            }

            // Deduct Money
            student.setWalletBalance(currentBalance - plan.getPrice());
            userRepository.save(student);

            // Create Order
            Order order = new Order();
            order.setStudent(student);
            order.setPlan(plan);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");

            int otp = (int) (Math.random() * 9000) + 1000;
            order.setDeliveryOtp(String.valueOf(otp));

            orderRepository.save(order);
            return "redirect:/student/dashboard?success=order_placed";
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null && "PENDING".equals(order.getStatus())) {
            User student = order.getStudent();
            Double currentBalance = (student.getWalletBalance() != null) ? student.getWalletBalance() : 0.0;
            student.setWalletBalance(currentBalance + order.getPlan().getPrice());
            userRepository.save(student);

            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
        return "redirect:/student/dashboard";
    }
}
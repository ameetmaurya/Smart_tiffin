package com.example.smart_tiffin.controller;

import com.example.smart_tiffin.model.Order;
import com.example.smart_tiffin.model.User;
import com.example.smart_tiffin.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private OrderRepository orderRepository;

    // DELIVERY DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        if (user == null || !"DELIVERY".equals(user.getRole())) {
            return "redirect:/";
        }

        List<Order> readyOrders = orderRepository.findByStatus("READY");
        List<Order> activeDeliveries = orderRepository.findByStatus("OUT_FOR_DELIVERY");

        model.addAttribute("user", user);
        model.addAttribute("readyOrders", readyOrders);
        model.addAttribute("activeDeliveries", activeDeliveries);

        return "delivery-dashboard";
    }

    // PICKUP ORDER (READY -> OUT_FOR_DELIVERY)
    @GetMapping("/pickup/{id}")
    public String pickupOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null && "READY".equals(order.getStatus())) {
            order.setStatus("OUT_FOR_DELIVERY");
            orderRepository.save(order);
        }
        return "redirect:/delivery/dashboard";
    }

    // COMPLETE DELIVERY WITH OTP VALIDATION
    @PostMapping("/complete")
    public String completeDelivery(@RequestParam Long orderId,
                                   @RequestParam String otp,
                                   HttpSession session,
                                   Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"DELIVERY".equals(user.getRole())) {
            return "redirect:/";
        }

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {
            if ("OUT_FOR_DELIVERY".equals(order.getStatus())) {
                if (order.getDeliveryOtp().equals(otp)) {
                    order.setStatus("DELIVERED");
                    orderRepository.save(order);
                    model.addAttribute("successMessage", "✅ Order delivered successfully!");
                } else {
                    model.addAttribute("errorMessage", "❌ Wrong OTP! Delivery not completed.");
                }
            } else {
                model.addAttribute("errorMessage", "Order is not ready for delivery.");
            }
        } else {
            model.addAttribute("errorMessage", "Order not found.");
        }

        // Reload dashboard with updated orders
        List<Order> readyOrders = orderRepository.findByStatus("READY");
        List<Order> activeDeliveries = orderRepository.findByStatus("OUT_FOR_DELIVERY");

        model.addAttribute("user", user);
        model.addAttribute("readyOrders", readyOrders);
        model.addAttribute("activeDeliveries", activeDeliveries);

        return "delivery-dashboard";
    }
}

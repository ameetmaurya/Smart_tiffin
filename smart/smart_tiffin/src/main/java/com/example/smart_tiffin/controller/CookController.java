package com.example.smart_tiffin.controller;



import com.example.smart_tiffin.model.Order;
import com.example.smart_tiffin.model.TiffinPlan;
import com.example.smart_tiffin.model.User;
import com.example.smart_tiffin.repository.OrderRepository;
import com.example.smart_tiffin.repository.PlanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cook")
public class CookController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 1. Show the Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        // Security Check: Is user logged in and is a COOK?
        if (user == null || !"COOK".equals(user.getRole())) {
            return "redirect:/";
        }

        // Fetch data for this specific cook
        List<TiffinPlan> myPlans = planRepository.findByCookId(user.getId());
        List<Order> myOrders = orderRepository.findByPlanCookId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("plans", myPlans);
        model.addAttribute("orders", myOrders);

        return "cook-dashboard"; // Load HTML
    }

    // 2. Add a New Tiffin Plan
    @PostMapping("/add-plan")
    public String addPlan(@RequestParam String planName,
                          @RequestParam Double price,
                          @RequestParam String description,
                          HttpSession session) {

        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            TiffinPlan plan = new TiffinPlan();
            plan.setPlanName(planName);
            plan.setPrice(price);
            plan.setDescription(description);
            plan.setCook(user); // Link plan to this cook

            planRepository.save(plan);
        }
        return "redirect:/cook/dashboard";
    }

    // 3. Update Order Status (e.g., Cooking -> Ready)
    @GetMapping("/order-status/{orderId}/{status}")
    public String updateStatus(@PathVariable Long orderId, @PathVariable String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/cook/dashboard";
    }
}
package com.example.smart_tiffin.repository;

import com.example.smart_tiffin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStudentId(Long studentId);

    List<Order> findByPlanCookId(Long cookId);

    List<Order> findByStatus(String status);
    

    // 🔐 OTP verification
    Optional<Order> findByIdAndDeliveryOtp(Long id, String deliveryOtp);

    List<Order> findByStudentIdOrderByIdDesc(Long studentId);

    List<Order> findTop5ByStudentIdOrderByIdDesc(Long studentId);
}

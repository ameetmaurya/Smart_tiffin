package com.example.smart_tiffin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;

    private String status;

    // 🔐 OTP for delivery confirmation
    @Column(length = 6)
    private String deliveryOtp;

    // ✅ true when student receives order
    private boolean receivedByStudent;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private TiffinPlan plan;
}

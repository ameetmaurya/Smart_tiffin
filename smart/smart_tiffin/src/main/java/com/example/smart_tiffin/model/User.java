package com.example.smart_tiffin.model;


import jakarta.persistence.*;
import lombok.Data; // Auto-generates Getters/Setters

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String phone;

    // Roles: "STUDENT", "COOK", "DELIVERY"
    private String role;

    private String resetToken;
    private String kitchenLocation;
    private Double walletBalance = 0.0;

}
package com.example.smart_tiffin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tiffin_plans")
public class TiffinPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;
    private String description;
    private Double price;

    private String dietType; // VEG / NON-VEG

    private String imageUrl; // URL of meal image

    @ManyToOne
    @JoinColumn(name = "cook_id")
    private User cook;
}

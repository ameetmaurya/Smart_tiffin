package com.example.smart_tiffin.repository;

import com.example.smart_tiffin.model.TiffinPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<TiffinPlan, Long> {
    List<TiffinPlan> findByCookId(Long cookId); // Find all plans by a specific cook
}
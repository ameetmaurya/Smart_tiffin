package com.example.smart_tiffin.repository;

import com.example.smart_tiffin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
}
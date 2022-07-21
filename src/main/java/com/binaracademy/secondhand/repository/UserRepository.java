package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

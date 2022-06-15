package com.binaracademy.secondhand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.binaracademy.secondhand.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    @Query ("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);
}

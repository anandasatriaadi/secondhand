package com.binaracademy.secondhand.service;

import java.util.List;

import com.binaracademy.secondhand.model.User;

public interface UserService {
    User saveUser(User user);
    User getUser(String username);
    List<User> getAllUsers();
}

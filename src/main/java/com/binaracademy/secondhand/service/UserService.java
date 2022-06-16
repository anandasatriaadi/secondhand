package com.binaracademy.secondhand.service;

import java.util.List;

import com.binaracademy.secondhand.dto.UserDto;
import com.binaracademy.secondhand.model.User;

public interface UserService {
    User saveUser(UserDto userDto);
    User getUser(String username);
    List<User> getAllUsers();
}

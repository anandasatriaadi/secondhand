package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UserDto;
import com.binaracademy.secondhand.model.User;
import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);
    User getUser(String username);
    List<User> getAllUsers();
}

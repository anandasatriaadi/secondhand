package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UserUploadDto;
import com.binaracademy.secondhand.model.User;
import java.util.List;

public interface UserService {
    User saveUser(UserUploadDto userDto);
    User getUser(String email);
    User getUserId(Long id);
    boolean checkUser(String email);
    List<User> getAllUsers();
}

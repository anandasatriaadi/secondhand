package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UserResponseDto;
import com.binaracademy.secondhand.dto.UserUploadDto;
import java.util.List;

public interface UserService {
    UserResponseDto saveUser(UserUploadDto userDto);
    UserResponseDto getUser(String username);
    boolean checkUser(String username);
    List<UserResponseDto> getAllUsers();
}

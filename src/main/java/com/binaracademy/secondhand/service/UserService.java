package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ResponseUserDto;
import com.binaracademy.secondhand.dto.UploadUserDto;
import java.util.List;

public interface UserService {
    ResponseUserDto saveUser(UploadUserDto userDto);
    ResponseUserDto getUser(String username);
    boolean checkUser(String username);
    List<ResponseUserDto> getAllUsers();
}

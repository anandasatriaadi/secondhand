package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class UserRegisterDto {

    private String email;
    private String password;
    private String fullName;
}

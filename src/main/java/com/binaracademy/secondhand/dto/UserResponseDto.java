package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
    private String imageUrl;
}

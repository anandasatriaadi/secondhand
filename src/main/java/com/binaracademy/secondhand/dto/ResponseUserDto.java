package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class ResponseUserDto {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
}

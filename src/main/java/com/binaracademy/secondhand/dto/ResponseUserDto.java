package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class ResponseUserDto {

    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
}

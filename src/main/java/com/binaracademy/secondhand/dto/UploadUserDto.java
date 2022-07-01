package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class UploadUserDto {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
}
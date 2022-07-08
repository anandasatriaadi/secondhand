package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class UserUploadDto {

    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
}

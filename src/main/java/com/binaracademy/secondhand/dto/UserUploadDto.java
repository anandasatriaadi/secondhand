package com.binaracademy.secondhand.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserUploadDto {

    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
    private MultipartFile image;
}

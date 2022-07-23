package com.binaracademy.secondhand.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUploadDto {

    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
    private MultipartFile image;
}

package com.binaracademy.secondhand.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadProductDto {

    private String name;
    private String description;
    private Double price;
    private String address;
    private Long categoryId;
    private MultipartFile[] images;
}

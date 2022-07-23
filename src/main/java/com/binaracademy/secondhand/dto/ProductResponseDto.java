package com.binaracademy.secondhand.dto;

import com.binaracademy.secondhand.util.enums.ProductStatus;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String address;
    private String thumbnailUrl;
    private ProductStatus productStatus;
    private Long userId;
    private UserResponseDto userInfo;
    private Long categoryId;
}

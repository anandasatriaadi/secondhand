package com.binaracademy.secondhand.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String address;

    @Enumerated(EnumType.STRING)
    private String productStatus;

    private Long userId;
    private Long categoryId;
}

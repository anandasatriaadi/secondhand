package com.binaracademy.secondhand.dto;

import lombok.Data;

@Data
public class ProductDto {
	private Long id;
    private String name;
    private String description;
    private Double price;
    private String address;
	private Long userId;
	private Long categoryId;
}

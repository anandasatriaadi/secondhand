package com.binaracademy.secondhand.dto;

import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.util.enums.ProductStatus;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

@Data
public class ProductResponseImageDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String address;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private Long userId;
    private UserResponseDto userInfo;
    private Long categoryId;
    private Category category;
    private List<ProductImage> productImages;
}

package com.binaracademy.secondhand.dto;

import com.binaracademy.secondhand.util.enums.ProductStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUploadDto {

    private String name;
    private String description;
    private Double price;
    private String address;
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private MultipartFile[] images;
}

package com.binaracademy.secondhand.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.binaracademy.secondhand.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
}

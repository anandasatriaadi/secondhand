package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {}

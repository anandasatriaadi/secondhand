package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT p FROM ProductImage p WHERE p.productId = ?1")
    List<ProductImage> findImageByProductId(Long productId);
}

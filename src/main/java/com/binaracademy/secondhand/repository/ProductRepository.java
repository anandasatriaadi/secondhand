package com.binaracademy.secondhand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;

public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("SELECT p FROM ProductImage p WHERE p.productId = ?1")
    List<ProductImage> findAllProductImages(Long productId);
    
    @Query("SELECT p FROM ProductOffer p WHERE p.productId = ?1")
    List<ProductOffer> findAllProductOffers(Long productId);
}

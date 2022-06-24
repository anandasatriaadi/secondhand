package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> findAllAndSearch(String search, Pageable pageable);

    @Query("SELECT p FROM ProductImage p WHERE p.productId = ?1")
    List<ProductImage> findAllProductImages(Long productId);

    @Query("SELECT p FROM ProductOffer p WHERE p.productId = ?1")
    List<ProductOffer> findAllProductOffers(Long productId);

    @Query("SELECT p FROM Product p WHERE p.categoryId = ?1")
    List<Product> findProductsByCategory(Long categoryId, Pageable pageable);
}

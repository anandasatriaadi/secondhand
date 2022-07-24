package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.util.enums.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductStatus(ProductStatus productStatus, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productStatus = 'PUBLISHED' AND p.name LIKE %?1%")
    List<Product> findAllAndSearch(String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoryId = ?1 AND p.productStatus = 'PUBLISHED'")
    List<Product> findByCategory(Long categoryId, Pageable pageable);

    long countByUserIdAndProductStatus(Long userId, ProductStatus status);

    List<Product> findByUserId(Long userId, Pageable pageable);
}

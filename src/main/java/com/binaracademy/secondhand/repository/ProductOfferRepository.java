package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOfferRepository extends JpaRepository<ProductOffer, Long> {
    List<ProductOffer> findByProductId(Long productId);
    List<ProductOffer> findByUserId(Long userId);
}

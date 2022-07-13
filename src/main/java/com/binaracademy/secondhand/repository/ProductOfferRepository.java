package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductOfferRepository extends JpaRepository<ProductOffer, Long> {
    @Query("SELECT p FROM ProductOffer p WHERE p.userId = ?1")
    List<ProductOffer> findOfferByUserId(Long userId);
}

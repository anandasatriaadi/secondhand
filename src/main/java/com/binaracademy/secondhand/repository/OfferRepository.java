package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.Offer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByProductId(Long productId);
    List<Offer> findByBuyerId(Long buyerId);
    List<Offer> findBySellerId(Long sellerId);
}

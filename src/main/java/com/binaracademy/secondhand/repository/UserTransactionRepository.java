package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.UserTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, Long> {
    List<UserTransaction> findByBuyerId(Long buyerId);
    List<UserTransaction> findBySellerId(Long sellerId);
}

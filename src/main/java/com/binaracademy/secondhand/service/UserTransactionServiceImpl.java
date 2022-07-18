package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.UserTransaction;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.repository.UserTransactionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTransactionServiceImpl implements UserTransactionService {

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserTransaction saveUserTransaction(UserTransaction userTransaction) {
        return userTransactionRepository.save(userTransaction);
    }

    @Override
    public List<UserTransaction> getBuyerTransactions(String email) {
        Long buyerId = userRepository.findByEmail(email).getId();
        return userTransactionRepository.findByBuyerId(buyerId);
    }

    @Override
    public List<UserTransaction> getSellerTransactions(String email) {
        Long sellerId = userRepository.findByEmail(email).getId();
        return userTransactionRepository.findBySellerId(sellerId);
    }
}

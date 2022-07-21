package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.UserTransaction;
import java.util.List;

public interface UserTransactionService {
    UserTransaction saveUserTransaction(UserTransaction userTransaction);
    List<UserTransaction> getBuyerTransactions(String email);
    List<UserTransaction> getSellerTransactions(String email);
}

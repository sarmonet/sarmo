package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.TransactionSupport;
import java.util.List;

public interface TransactionSupportService {

    TransactionSupport addTransactionSupport(TransactionSupport transactionSupport);

    TransactionSupport getTransactionSupport(Long userId, Long listingId);

    TransactionSupport updateTransactionSupport(TransactionSupport transactionSupport);

    void removeTransactionSupport(Long userId, Long listingId);

    List<TransactionSupport> getTransactionSupportsByUserId(Long userId);

    TransactionSupport addTransactionSupport(Long userId, Long listingId, TransactionSupport transactionSupport);

    TransactionSupport updateTransactionSupport(Long userId, Long listingId, TransactionSupport transactionSupport);


}
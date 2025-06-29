package com.sarmo.userservice.service;

import com.sarmo.userservice.entity.TransactionSupport;
import com.sarmo.userservice.entity.compositeKey.TransactionSupportId;
import com.sarmo.userservice.repository.TransactionSupportRepository;
import com.sarmo.userservice.service.interfaces.TransactionSupportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionSupportServiceImpl implements TransactionSupportService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSupportServiceImpl.class);

    private final TransactionSupportRepository transactionSupportRepository;

    public TransactionSupportServiceImpl(TransactionSupportRepository transactionSupportRepository) {
        this.transactionSupportRepository = transactionSupportRepository;
    }

    @Override
    public TransactionSupport addTransactionSupport(TransactionSupport transactionSupport) {
        try {
            logger.info("Adding transaction support: {}", transactionSupport);
            return transactionSupportRepository.save(transactionSupport);
        } catch (Exception e) {
            logger.error("Error adding transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to add transaction support", e);
        }
    }

    @Override
    public TransactionSupport getTransactionSupport(Long userId, Long listingId) {
        try {
            logger.debug("Getting transaction support for user {} and listing {}", userId, listingId);
            TransactionSupportId id = new TransactionSupportId(userId, listingId);
            Optional<TransactionSupport> transactionSupport = transactionSupportRepository.findById(id);
            return transactionSupport.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to get transaction support", e);
        }
    }

    @Override
    public TransactionSupport updateTransactionSupport(TransactionSupport transactionSupport) {
        try {
            logger.info("Updating transaction support: {}", transactionSupport);
            return transactionSupportRepository.save(transactionSupport);
        } catch (Exception e) {
            logger.error("Error updating transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to update transaction support", e);
        }
    }

    @Override
    public void removeTransactionSupport(Long userId, Long listingId) {
        try {
            logger.info("Removing transaction support for user {} and listing {}", userId, listingId);
            TransactionSupportId id = new TransactionSupportId(userId, listingId);
            transactionSupportRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error removing transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to remove transaction support", e);
        }
    }

    @Override
    public List<TransactionSupport> getTransactionSupportsByUserId(Long userId) {
        try {
            logger.debug("Getting transaction supports by user id: {}", userId);
            return transactionSupportRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Error getting transaction supports by user id: {}", e.getMessage());
            throw new RuntimeException("Failed to get transaction supports by user id", e);
        }
    }

    @Override
    public TransactionSupport addTransactionSupport(Long userId, Long listingId, TransactionSupport transactionSupport) {
        try {
            logger.debug("Adding transaction support for user {} and listing {}", userId, listingId);
            transactionSupport.setUserId(userId);
            transactionSupport.setListingId(listingId);
            return transactionSupportRepository.save(transactionSupport);
        } catch (Exception e) {
            logger.error("Error adding transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to add transaction support", e);
        }
    }

    @Override
    public TransactionSupport updateTransactionSupport(Long userId, Long listingId, TransactionSupport transactionSupport) {
        try {
            logger.debug("Updating transaction support for user {} and listing {}", userId, listingId);
            transactionSupport.setUserId(userId);
            transactionSupport.setListingId(listingId);
            return transactionSupportRepository.save(transactionSupport);
        } catch (Exception e) {
            logger.error("Error updating transaction support: {}", e.getMessage());
            throw new RuntimeException("Failed to update transaction support", e);
        }
    }


}
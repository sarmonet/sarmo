package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.TransactionSupport;
import com.sarmo.userservice.entity.compositeKey.TransactionSupportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionSupportRepository extends JpaRepository<TransactionSupport, TransactionSupportId> {
    List<TransactionSupport> findByUserId(Long userId);
}
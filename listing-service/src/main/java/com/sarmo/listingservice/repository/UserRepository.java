package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

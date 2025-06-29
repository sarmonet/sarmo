package com.sarmo.userservice.repository;


import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.enums.AccountStatus;
import com.sarmo.userservice.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
    List<User> findByCountryAndCity(String country, String city);
    List<User> findByAccountStatus(AccountStatus accountStatus);
    List<User> findByUserStatus(UserStatus userStatus);
    User findByEmail(String email);

}

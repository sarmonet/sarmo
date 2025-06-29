package com.sarmo.authservice.service;

import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.entity.Role; // Импорт сущности Role
import com.sarmo.authservice.repository.UserRepository;
import com.sarmo.authservice.repository.RoleRepository; // Импорт репозитория Role
import com.sarmo.authservice.dto.UpdateUserRequest; // Импорт вашего DTO для обновления

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set; // Импорт Set
import java.util.HashSet; // Импорт HashSet
import java.util.Collections; // Импорт Collections

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User getUserByProviderUserId(String providerUserId) {
        return userRepository.findByProviderUserId(providerUserId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public User createUser(User user) {
        // Логика создания пользователя...
        return userRepository.save(user);
    }

    // Модифицированный метод updateUser, принимающий UpdateUserRequest DTO
    @Transactional
    public User updateUser(Long id, UpdateUserRequest updateRequest) { // Метод принимает DTO
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            user.setPassword(updateRequest.getPassword()); // !!! ОСТОРОЖНО!
        }

        if (updateRequest.getTwoFactorEnabled() != null) {
            user.setTwoFactorEnabled(updateRequest.getTwoFactorEnabled());
        }


        if (updateRequest.getRoleId() != null) {
            Long roleIdToSet = updateRequest.getRoleId();

            // Находим управляемую сущность Role из базы данных по этому ID
            Role managedRole = roleRepository.findById(roleIdToSet)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found with id: " + roleIdToSet));

            Set<Role> newRoles = new HashSet<>();
            newRoles.add(managedRole);


            user.setRoles(newRoles);
        }



        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        userRepository.deleteById(id);
    }
}
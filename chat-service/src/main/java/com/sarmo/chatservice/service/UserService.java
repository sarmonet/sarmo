package com.sarmo.chatservice.service;

import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.repository.UserRepository;
import com.sarmo.chatservice.dto.UserResponseDTO;
import com.sarmo.chatservice.dto.UserCreationRequestDTO;
import com.sarmo.chatservice.dto.UserUpdateRequestDTO;
import com.sarmo.chatservice.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO createUser(UserCreationRequestDTO userDTO) {
        logger.info("Creating user from DTO: {}", userDTO);

        User user = UserMapper.toUser(userDTO);

        try {
            User createdUser = userRepository.save(user);
            logger.info("User created with ID: {}", createdUser.getId());
            return UserMapper.toUserResponseDTO(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user from DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        logger.info("Getting user by ID: {}", id);
        try {
            User user = userRepository.findById(id).orElseThrow(() -> {
                logger.warn("User with ID {} not found", id);
                return new NoSuchElementException("User not found");
            });
            return UserMapper.toUserResponseDTO(user);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error getting user", e);
        }
    }

    @Transactional(readOnly = true)
    public User getUserByIdEntity(Long id) {
        logger.info("Getting user by ID: {}", id);
        try {
            return userRepository.findById(id).orElseThrow(() -> {
                logger.warn("User with ID {} not found", id);
                return new NoSuchElementException("User not found");
            });
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error getting user", e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Getting all users (without pagination)");
        try {
            List<User> users = userRepository.findAll();
            logger.info("Successfully retrieved {} users", users.size());
            return UserMapper.toUserResponseDTOs(users);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting all users", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable) {
        logger.info("Getting users with pagination. Page: {}, Size: {}, Sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        try {
            Page<User> userPage = userRepository.findAll(pageable);
            logger.info("Successfully retrieved {} users for page {} (size {}). Total elements: {}",
                    userPage.getNumberOfElements(), pageable.getPageNumber(), pageable.getPageSize(), userPage.getTotalElements());

            return userPage.map(UserMapper::toUserResponseDTO);
        } catch (Exception e) {
            logger.error("Error getting users with pagination: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting users with pagination", e);
        }
    }


    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO userDetailsDTO) {
        logger.info("Updating user with ID {} from DTO: {}", userId, userDetailsDTO);

        User existingUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found for update", userId);
            return new NoSuchElementException("User not found");
        });

        UserMapper.updateUserFromDto(userDetailsDTO, existingUser);

        try {
            User updatedUser = userRepository.save(existingUser);
            logger.info("User with ID {} updated successfully", userId);
            return UserMapper.toUserResponseDTO(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user by ID: {}", id);
        try {
            userRepository.deleteById(id);
            logger.info("User with ID {} deleted", id);
        } catch (Exception e) {
            logger.error("Error deleting user by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Transactional
    public UserResponseDTO blockUser(Long userId, Long blockedUserId) {
        logger.info("Blocking user with ID {} by user with ID {}", blockedUserId, userId);
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> {
                logger.warn("User with ID {} not found for blocking", userId);
                return new NoSuchElementException("User doing the blocking not found");
            });

            Set<Long> blockedUsers = user.getBlockedUserIds();
            if (blockedUsers.add(blockedUserId)) {
                user.setBlockedUserIds(blockedUsers);
                User updatedUser = userRepository.save(user);
                logger.info("User with ID {} successfully blocked user with ID {}", userId, blockedUserId);
                return UserMapper.toUserResponseDTO(updatedUser);
            } else {
                logger.warn("User with ID {} was already blocked by user with ID {}", blockedUserId, userId);
                return UserMapper.toUserResponseDTO(user);
            }

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error blocking user {} by user {}: {}", blockedUserId, userId, e.getMessage(), e);
            throw new RuntimeException("Error blocking user", e);
        }
    }

    @Transactional
    public UserResponseDTO unblockUser(Long userId, Long blockedUserId) {
        logger.info("Unblocking user with ID {} by user with ID {}", blockedUserId, userId);
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> {
                logger.warn("User with ID {} not found for unblocking", userId);
                return new NoSuchElementException("User doing the unblocking not found");
            });

            Set<Long> blockedUsers = user.getBlockedUserIds();
            if (blockedUsers.remove(blockedUserId)) {
                user.setBlockedUserIds(blockedUsers);
                User updatedUser = userRepository.save(user);
                logger.info("User with ID {} successfully unblocked user with ID {}", userId, blockedUserId);
                return UserMapper.toUserResponseDTO(updatedUser);
            } else {
                logger.warn("User with ID {} was not blocked by user with ID {}", blockedUserId, userId);
                return UserMapper.toUserResponseDTO(user);
            }

        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error unblocking user {} by user {}: {}", blockedUserId, userId, e.getMessage(), e);
            throw new RuntimeException("Error unblocking user", e);
        }
    }

    @Transactional(readOnly = true)
    public Set<Long> getBlockedUsers(Long userId) {
        logger.info("Getting blocked users for user with ID {}", userId);
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> {
                logger.warn("User with ID {} not found for getting blocked users", userId);
                return new NoSuchElementException("User not found");
            });
            Set<Long> blockedUsers = user.getBlockedUserIds();
            logger.info("Successfully retrieved {} blocked users for user with ID {}", blockedUsers.size(), userId);
            return blockedUsers;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting blocked users for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error getting blocked users", e);
        }
    }

    public User getUserByEmail(String email) {
        logger.info("Getting user by email: {}", email);
        try {
            return userRepository.findByEmail(email).orElseThrow(() -> {
                logger.warn("User with email {} not found", email);
                return new NoSuchElementException("User not found");
            });
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user by email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error getting user by email", e);
        }
    }


}
package com.sarmo.chatservice.controller;

import com.sarmo.chatservice.entity.User; // Пока оставляем импорт сущности, т.к. она используется в getBlockedUsers
import com.sarmo.chatservice.service.UserService;
import com.sarmo.chatservice.dto.UserResponseDTO; // Импорт DTO
import com.sarmo.chatservice.dto.UserCreationRequestDTO;
import com.sarmo.chatservice.dto.UserUpdateRequestDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/chat/user")
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    // Возвращаем UserResponseDTO
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        logger.info("Getting profile for current authenticated user");
        Long userId = getCurrentUserId();
        try {
            // Сервис теперь возвращает UserResponseDTO
            UserResponseDTO userDTO = userService.getUserById(userId);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.warn("Authenticated user with ID {} not found: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error getting profile for user {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    // Принимаем UserUpdateRequestDTO, Возвращаем UserResponseDTO
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@RequestBody UserUpdateRequestDTO userDetailsDTO) {
        logger.info("Updating profile for current authenticated user from DTO");
        Long userId = getCurrentUserId();
        try {
            // Передаем ID и DTO обновления в сервис
            UserResponseDTO updatedUserDTO = userService.updateUser(userId, userDetailsDTO);
            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить, если пользователь не найден
            logger.warn("Update profile failed: User with ID {} not found: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error updating profile for user {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/me/blocked/{blockedUserId}")
    @PreAuthorize("isAuthenticated()")
    // Возвращаем UserResponseDTO (обновленного пользователя)
    public ResponseEntity<UserResponseDTO> blockUserForCurrentUser(@PathVariable Long blockedUserId) {
        logger.info("Current user {} is attempting to block user with ID: {}", getCurrentUserId(), blockedUserId);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис теперь возвращает UserResponseDTO
            UserResponseDTO updatedUserDTO = userService.blockUser(currentUserId, blockedUserId);
            logger.info("User with ID {} successfully blocked user with ID {}", currentUserId, blockedUserId);
            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить, если блокирующий или блокируемый пользователь не найден
            logger.warn("Block user failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error blocking user {} by user {}: {}", blockedUserId, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/me/blocked/{blockedUserId}")
    @PreAuthorize("isAuthenticated()")
    // Возвращаем UserResponseDTO (обновленного пользователя)
    public ResponseEntity<UserResponseDTO> unblockUserForCurrentUser(@PathVariable Long blockedUserId) {
        logger.info("Current user {} is attempting to unblock user with ID: {}", getCurrentUserId(), blockedUserId);
        Long currentUserId = getCurrentUserId();
        try {
            // Сервис теперь возвращает UserResponseDTO
            UserResponseDTO updatedUserDTO = userService.unblockUser(currentUserId, blockedUserId);
            logger.info("User with ID {} successfully unblocked user with ID {}", currentUserId, blockedUserId);
            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить
            logger.warn("Unblock user failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error unblocking user {} by user {}: {}", blockedUserId, currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me/blocked")
    @PreAuthorize("isAuthenticated()")
    // Сервис возвращает Set<Long>, оставляем как есть
    public ResponseEntity<Set<Long>> getBlockedUsersForCurrentUser() {
        logger.info("Getting blocked users for current authenticated user");
        Long currentUserId = getCurrentUserId();
        try {
            Set<Long> blockedUsers = userService.getBlockedUsers(currentUserId);
            logger.info("Successfully retrieved {} blocked users for user ID {}", blockedUsers.size(), currentUserId);
            return new ResponseEntity<>(blockedUsers, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить
            logger.warn("Get blocked users failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error getting blocked users for user {}: {}", currentUserId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Принимаем UserCreationRequestDTO, Возвращаем UserResponseDTO
    public ResponseEntity<UserResponseDTO> createUserAdmin(@RequestBody UserCreationRequestDTO userDTO) {
        logger.info("Admin is creating a new user from DTO");
        try {
            // Передаем DTO создания в сервис
            UserResponseDTO createdUserDTO = userService.createUser(userDTO);
            logger.info("Admin successfully created user with ID {}", createdUserDTO.getId());
            return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) { // Сервис может бросить при отсутствии пароля
            logger.warn("Admin create user failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        catch (RuntimeException e) { // Обработка других ошибок, включая возможное нарушение уникальности
            logger.error("Error creating user by admin from DTO: {}", e.getMessage(), e);
            // Можно добавить обработку DataIntegrityViolationException для более точного ответа
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    // Возвращаем Page<UserResponseDTO>
    public ResponseEntity<Page<UserResponseDTO>> getAllUsersPaginatedAdmin(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        logger.info("Admin request to get all users with pagination. Page: {}, Size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        try {
            // Сервис теперь возвращает Page<UserResponseDTO>
            Page<UserResponseDTO> userPage = userService.getAllUsersPaginated(pageable);

            logger.info("Admin successfully retrieved {} users for page {} (size {}). Total elements: {}",
                    userPage.getNumberOfElements(), page, size, userPage.getTotalElements());

            return new ResponseEntity<>(userPage, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("Error retrieving users for admin (pagination): {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    // Возвращаем List<UserResponseDTO>
    public ResponseEntity<List<UserResponseDTO>> getAllUsersAdmin() {
        logger.info("Admin is getting all users (without pagination)");
        try {
            // Сервис теперь возвращает List<UserResponseDTO>
            List<UserResponseDTO> usersDTO = userService.getAllUsers();
            logger.info("Admin successfully retrieved {} users", usersDTO.size());
            return new ResponseEntity<>(usersDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error getting all users by admin: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Возвращаем UserResponseDTO
    public ResponseEntity<UserResponseDTO> getUserByIdAdmin(@PathVariable Long id) {
        logger.info("Admin is getting user by ID {}", id);
        try {
            // Сервис теперь возвращает UserResponseDTO
            UserResponseDTO userDTO = userService.getUserById(id);
            logger.info("Admin successfully retrieved user with ID {}", id);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить
            logger.warn("Admin: User with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error getting user {} by admin: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Принимаем UserUpdateRequestDTO, Возвращаем UserResponseDTO
    public ResponseEntity<UserResponseDTO> updateUserAdmin(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userDetailsDTO) {
        logger.info("Admin is updating user with ID {} from DTO", id);
        try {
            // Передаем ID и DTO обновления в сервис
            UserResponseDTO updated = userService.updateUser(id, userDetailsDTO);
            logger.info("Admin successfully updated user with ID {}", id);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (NoSuchElementException e) { // Сервис может бросить
            logger.warn("Admin: Update user failed. User with ID {} not found: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            logger.error("Error updating user {} by admin from DTO: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Сервис возвращает void, оставляем как есть
    public ResponseEntity<Void> deleteUserAdmin(@PathVariable Long id) {
        logger.info("Admin is deleting user with ID {}", id);
        try {
            userService.deleteUser(id); // Сервис может бросить NoSuchElementException, если добавить проверку там
            logger.info("Admin successfully deleted user with ID {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) { // Обработка ошибок (в т.ч. если пользователь не найден при удалении)
            logger.error("Error deleting user {} by admin: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
                throw new IllegalStateException("Authenticated principal's name is not a valid user ID format", e);
            }
        }
        logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous");
        throw new IllegalStateException("User is not authenticated or principal is not as expected");
    }
}
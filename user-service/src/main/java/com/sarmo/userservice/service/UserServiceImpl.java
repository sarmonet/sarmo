package com.sarmo.userservice.service;

import com.sarmo.kafka.dto.UserImageData;
import com.sarmo.kafka.dto.UserRegistrationData; // Импортируем UserRegistrationData
import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.producer.UserImageProducer;
import com.sarmo.userservice.producer.UserUpdateProducer; // Импортируем UserUpdateProducer
import com.sarmo.userservice.repository.UserRepository;
import com.sarmo.userservice.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserImageProducer userImageProducer;
    private final UserUpdateProducer userUpdateProducer; // Добавляем UserUpdateProducer

    public UserServiceImpl(UserRepository userRepository, UserImageProducer userImageProducer, UserUpdateProducer userUpdateProducer) {
        this.userRepository = userRepository;
        this.userImageProducer = userImageProducer;
        this.userUpdateProducer = userUpdateProducer;
    }

    @Override
    public User createUser(User user) {
        try {
            logger.info("Creating user: {}", user);
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public User getUserById(Long id) {
        try {
            logger.debug("Getting user by id: {}", id);
            Optional<User> user = userRepository.findById(id);
            return user.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting user by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to get user by id", e);
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            logger.info("Updating user: {}", user);
            User updatedUser = userRepository.save(user);

            // Создаем UserRegistrationData для отправки в Kafka
            UserRegistrationData userUpdateData = new UserRegistrationData();
            userUpdateData.setUserId(updatedUser.getId());
            userUpdateData.setFirstName(updatedUser.getFirstName());
            userUpdateData.setLastName(updatedUser.getLastName());
            // Определяем emailOrPhone: приоритет email, затем phoneNumber
            if (StringUtils.hasText(updatedUser.getEmail())) {
                userUpdateData.setEmailOrPhone(updatedUser.getEmail());
            } else if (StringUtils.hasText(updatedUser.getPhoneNumber())) {
                userUpdateData.setEmailOrPhone(updatedUser.getPhoneNumber());
            }

            // Отправляем сообщение об обновлении в Kafka
            userUpdateProducer.sendUserUpdateMessage(userUpdateData);
            logger.info("Sent user update message for user ID {}: {}", updatedUser.getId(), userUpdateData);

            return updatedUser;
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            logger.info("Deleting user by id: {}", id);
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting user by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user by id", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            logger.info("Getting all users");
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            logger.debug("Getting user by email: {}", email);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            logger.error("Error getting user by email {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to get user by email", e);
        }
    }

    @Override
    public void addDocument(Long userId, String documentUrl) {
        try {
            logger.debug("Adding document {} for user {}", documentUrl, userId);
            User user = getUserById(userId);
            if (user != null) {
                user.getDocuments().add(documentUrl);
                userRepository.save(user);
            } else {
                logger.warn("User with id {} not found, document not added", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error adding document: {}", e.getMessage());
            throw new RuntimeException("Failed to add document", e);
        }
    }

    @Override
    public void removeDocument(Long userId, String documentUrl) {
        try {
            logger.debug("Removing document {} for user {}", documentUrl, userId);
            User user = getUserById(userId);
            if (user != null) {
                user.getDocuments().remove(documentUrl);
                userRepository.save(user);
                // Аналогично addDocument, если нужно, добавьте отправку сообщения в Kafka
            } else {
                logger.warn("User with id {} not found, document not removed", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error removing document: {}", e.getMessage());
            throw new RuntimeException("Failed to remove document", e);
        }
    }

    @Override
    public List<String> getDocuments(Long userId) {
        try {
            logger.debug("Getting documents for user {}", userId);
            User user = getUserById(userId);
            if (user != null) {
                return user.getDocuments();
            } else {
                logger.warn("User with id {} not found, documents not retrieved", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting documents: {}", e.getMessage());
            throw new RuntimeException("Failed to get documents", e);
        }
    }

    @Override
    public void updateProfilePicture(Long userId, String profilePictureUrl) {
        try {
            logger.info("Updating profile picture for user {} to {}", userId, profilePictureUrl);
            User user = getUserById(userId);
            if (user != null) {
                user.setProfilePictureUrl(profilePictureUrl);
                userImageProducer.sendUserImageMessage(new UserImageData(userId, profilePictureUrl));
                userRepository.save(user);
            } else {
                logger.warn("User with id {} not found, profile picture not updated", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error updating profile picture: {}", e.getMessage());
            throw new RuntimeException("Failed to update profile picture", e);
        }
    }

    @Override
    public User partialUpdateUser(Long userId, User updatedUser) {
        try {
            logger.info("Partially updating user with id {}: {}", userId, updatedUser);
            User existingUser = getUserById(userId);
            if (existingUser == null) {
                logger.warn("User with id {} not found, cannot perform partial update", userId);
                throw new RuntimeException("User not found");
            }

            boolean isDataChanged = false; // Флаг для отслеживания изменений

            if (StringUtils.hasText(updatedUser.getFirstName())) {
                existingUser.setFirstName(updatedUser.getFirstName());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getLastName())) {
                existingUser.setLastName(updatedUser.getLastName());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getEmail())) {
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setPhoneNumber(null); // Если обновляем email, сбрасываем номер телефона
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getPhoneNumber())) { // Добавлено для обновления номера телефона
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                existingUser.setEmail(null); // Если обновляем телефон, сбрасываем email
                isDataChanged = true;
            }
            if (updatedUser.getBirthDate() != null) {
                existingUser.setBirthDate(updatedUser.getBirthDate());
                isDataChanged = true;
            }
            if (updatedUser.getUserStatus() != null) {
                existingUser.setUserStatus(updatedUser.getUserStatus());
                isDataChanged = true;
            }
            if (updatedUser.getAccountStatus() != null) {
                existingUser.setAccountStatus(updatedUser.getAccountStatus());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getCountry())) {
                existingUser.setCountry(updatedUser.getCountry());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getCity())) {
                existingUser.setCity(updatedUser.getCity());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getFullAddress())) {
                existingUser.setFullAddress(updatedUser.getFullAddress());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getProfilePictureUrl())) {
                existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
                // В этом случае также отправляем сообщение через userImageProducer,
                // но для общих обновлений userUpdateProducer также может быть вызван
                userImageProducer.sendUserImageMessage(new UserImageData(userId, updatedUser.getProfilePictureUrl()));
                isDataChanged = true;
            }
            if (updatedUser.getDocuments() != null) {
                existingUser.setDocuments(updatedUser.getDocuments());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getNotificationSettingsId())) {
                existingUser.setNotificationSettingsId(updatedUser.getNotificationSettingsId());
                isDataChanged = true;
            }
            if (StringUtils.hasText(updatedUser.getUserSettingsId())) {
                existingUser.setUserSettingsId(updatedUser.getUserSettingsId());
                isDataChanged = true;
            }

            User savedUser = userRepository.save(existingUser);

            // Отправляем сообщение в Kafka только если данные были изменены
            if (isDataChanged) {
                UserRegistrationData userUpdateData = new UserRegistrationData();
                userUpdateData.setUserId(savedUser.getId());
                userUpdateData.setFirstName(savedUser.getFirstName());
                userUpdateData.setLastName(savedUser.getLastName());
                if (StringUtils.hasText(savedUser.getEmail())) {
                    userUpdateData.setEmailOrPhone(savedUser.getEmail());
                } else if (StringUtils.hasText(savedUser.getPhoneNumber())) {
                    userUpdateData.setEmailOrPhone(savedUser.getPhoneNumber());
                }

                userUpdateProducer.sendUserUpdateMessage(userUpdateData);
                logger.info("Sent partial user update message for user ID {}: {}", savedUser.getId(), userUpdateData);
            }

            return savedUser;

        } catch (Exception e) {
            logger.error("Error partially updating user with id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to partially update user", e);
        }
    }
}

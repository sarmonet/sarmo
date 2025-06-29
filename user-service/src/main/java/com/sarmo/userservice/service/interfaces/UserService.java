package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.TransactionSupport;
import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.entity.UserFavoriteListing;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    void addDocument(Long userId, String documentUrl);

    void removeDocument(Long userId, String documentUrl);

    List<String> getDocuments(Long userId);

    void updateProfilePicture(Long userId, String profilePictureUrl);

    User partialUpdateUser(Long userId, User updatedUser);
}
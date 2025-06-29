package com.sarmo.chatservice.mapper;

import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.dto.UserResponseDTO;
import com.sarmo.chatservice.dto.UserCreationRequestDTO;
import com.sarmo.chatservice.dto.UserUpdateRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static User toUser(UserCreationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return user;
    }

    public static void updateUserFromDto(UserUpdateRequestDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(dto.getProfilePictureUrl());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
    }


    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfilePictureUrl(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public static List<UserResponseDTO> toUserResponseDTOs(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }
}
package com.sarmo.noticeservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;

public class UserDto {
    private Long id;

    private String profilePictureUrl;
    private String firstName;
    private String lastName;
}

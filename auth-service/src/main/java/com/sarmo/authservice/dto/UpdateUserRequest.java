package com.sarmo.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern; // Import Pattern annotation


// DTO for partial user update
// Used in update requests (e.g., PATCH)
public class UpdateUserRequest {

    // Fields that the client can provide for update.
    // Using wrapper types to distinguish 'not provided' (null) from 'provided value'.

    // User's name (optional)
    private String name;

    // Email (optional). Validate format if email is provided.
    @Email(message = "Invalid email format")
    private String email;

    // Phone number (optional). Add @Pattern for format validation.
    // Regex allows optional '+', digits, spaces, hyphens, and parentheses.
    // Adjust the regex based on required phone number formats (e.g., specific countries).
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{6,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    // Password (optional). Password updates should be handled securely, ideally in a separate flow.
    @Size(min = 8, message = "Password must be at least 8 characters long if provided")
    private String password; // WARNING: This field will contain the RAW PASSWORD from the request!

    // Two-factor authentication enabled flag (optional).
    // Using Boolean (wrapper) to distinguish null (not provided) from true/false.
    private Boolean twoFactorEnabled;

    // ID of a single role to set for the user, completely replacing previous roles.
    // Null means roles are not changed.
    private Long roleId;


    // Default constructor (needed for Spring deserialization)
    public UpdateUserRequest() {
    }

    // Getters and Setters for all fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}
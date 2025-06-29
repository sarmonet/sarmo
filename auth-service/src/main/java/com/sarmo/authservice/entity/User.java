package com.sarmo.authservice.entity;

import jakarta.persistence.*;
import com.sarmo.authservice.enums.AuthProvider;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String providerUserId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber; // Добавляем поле phoneNumber

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;

    public User() {}

    public User(String providerUserId, String email, String phoneNumber, String password, String name, AuthProvider authProvider, Set<Role> roles, boolean twoFactorEnabled) {
        this.providerUserId = providerUserId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.authProvider = authProvider;
        this.roles = roles;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public User(String providerUserId, String email, String phoneNumber, String password, String name, AuthProvider authProvider, Set<Role> roles) {
        this.providerUserId = providerUserId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.authProvider = authProvider;
        this.roles = roles;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
package com.sarmo.authservice.security;

import com.sarmo.authservice.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails, OAuth2User {

    private Long id;
    private String email;
    private String phoneNumber;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        UserPrincipal userPrincipal = new UserPrincipal(user.getId(), user.getPassword(), authorities);

        if(user.getEmail() != null) {
            userPrincipal.setEmail(user.getEmail());
        }else {
            userPrincipal.setPhoneNumber(user.getPhoneNumber());
        }
        return userPrincipal;

    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        if (email != null) {
            return email;
        } else {
            return phoneNumber;
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
package com.sarmo.authservice.security;

import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.repository.UserRepository;
import com.sarmo.authservice.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;

        if (username.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            // Если username - email
            user = userRepository.findByEmail(username).orElse(null);
        } else if (username.matches("^\\+?[1-9]\\d{1,14}$")){
            //Если username - номер телефона
            user = userRepository.findByPhoneNumber(username).orElse(null);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return UserPrincipal.create(user);
    }
}
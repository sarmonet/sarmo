package com.sarmo.authservice.security;

import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.enums.AuthProvider;
import com.sarmo.authservice.repository.UserRepository;
import com.sarmo.authservice.security.oauth2.OAuth2UserInfo;
import com.sarmo.authservice.security.oauth2.OAuth2UserInfoFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getAuthProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new OAuth2AuthenticationException("Looks like you're signed up with " + user.getAuthProvider() + " account. Please use your " + user.getAuthProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setAuthProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setPassword("oauth2"); // Установка пароля для пользователя, зарегистрированного через OAuth2
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        return userRepository.save(existingUser);
    }
}
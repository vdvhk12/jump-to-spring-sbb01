package org.example.jtsb01.user.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.user.entity.SiteUser;
import org.example.jtsb01.user.repository.SiteUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final SiteUserRepository siteUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");

        siteUserRepository.findByUsernameAndEmail(name, email)
            .ifPresentOrElse(
                user -> {
                    // 이미 존재하는 경우 처리
                    logger.info("User already exists with name = {} and email = {}", name, email);
                },
                () -> {
                    // 존재하지 않으면 저장
                    siteUserRepository.save(SiteUser.builder()
                        .username(name)
                        .email(email)
                        .build());
                    logger.info("New user created with name = {} and email = {}", name, email);
                }
            );
        return new DefaultOAuth2User(oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "sub");
    }
}

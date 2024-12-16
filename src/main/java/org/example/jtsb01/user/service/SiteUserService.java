package org.example.jtsb01.user.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.global.exception.PasswordNotMatchException;
import org.example.jtsb01.user.entity.SiteUser;
import org.example.jtsb01.user.model.PasswordForm;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.model.SiteUserForm;
import org.example.jtsb01.user.model.UserRole;
import org.example.jtsb01.user.repository.SiteUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteUserService {

    private final SiteUserRepository siteUserRepository;
    private final PasswordEncoder passwordEncoder;

    public void createSiteUser(SiteUserForm siteUserForm) {
        siteUserRepository.save(SiteUser.builder()
            .username(siteUserForm.getUsername())
            .password(passwordEncoder.encode(siteUserForm.getPassword1()))
            .email(siteUserForm.getEmail())
            .userRole(UserRole.USER)
            .build());
    }

    public SiteUserDto getSiteUser(String username) {
        return SiteUserDto.fromEntity(siteUserRepository.findByUsername(username)
            .orElseThrow(() -> new DataNotFoundException("site user not found")));
    }

    public void updatePassword(Long id, PasswordForm passwordForm) {
        SiteUser siteUser = siteUserRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("site user not found"));

        siteUserRepository.save(SiteUser.builder()
            .id(siteUser.getId())
            .username(siteUser.getUsername())
            .password(passwordEncoder.encode(passwordForm.getNewPassword()))
            .email(siteUser.getEmail())
            .userRole(UserRole.USER)
            .build());
    }

    public void verifyPassword(Long id, PasswordForm passwordForm) {
        SiteUser siteUser = siteUserRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("site user not found"));

        if (!passwordEncoder.matches(passwordForm.getOldPassword(), siteUser.getPassword())) {
            throw new PasswordNotMatchException("기존 비밀번호가 일치하지 않습니다.");
        }
    }
}

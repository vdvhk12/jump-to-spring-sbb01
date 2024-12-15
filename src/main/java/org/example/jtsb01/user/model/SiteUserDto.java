package org.example.jtsb01.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.user.entity.SiteUser;

@Getter
@Setter
@Builder
public class SiteUserDto {

    private Long id;
    private String username;
    private String email;

    public static SiteUserDto fromEntity(SiteUser siteUser) {
        return SiteUserDto.builder()
            .id(siteUser.getId())
            .username(siteUser.getUsername())
            .email(siteUser.getEmail())
            .build();
    }

    public static SiteUser fromDto(SiteUserDto siteUserDto) {
        return SiteUser.builder()
            .id(siteUserDto.getId())
            .username(siteUserDto.getUsername())
            .email(siteUserDto.getEmail())
            .build();
    }
}

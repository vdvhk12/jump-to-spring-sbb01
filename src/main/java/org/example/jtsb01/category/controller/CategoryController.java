package org.example.jtsb01.category.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.category.model.CategoryForm;
import org.example.jtsb01.category.service.CategoryService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final SiteUserService siteUserService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String create(CategoryForm categoryForm) {
        return "category_form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String create(@Valid CategoryForm categoryForm, BindingResult bindingResult,
        Principal principal) {
        if (bindingResult.hasErrors()) {
            return "category_form";
        }
        String username = getUsernameFromPrincipal(principal);
        categoryService.createCategory(categoryForm);
        return "redirect:/question/list";
    }

    // 사용자 이름 꺼내오는 메서드
    private String getUsernameFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            return (String) oauth2User.getAttributes().get("name");
        }
        return principal.getName();
    }

    // 권한 체크 메서드
    private void checkUserPermission(String principalUsername, String targetUsername, String action) {
        if (!principalUsername.equals(targetUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, action + " 권한이 없습니다.");
        }
    }
}

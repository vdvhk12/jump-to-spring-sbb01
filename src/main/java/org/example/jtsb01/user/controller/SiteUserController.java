package org.example.jtsb01.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.user.model.SiteUserForm;
import org.example.jtsb01.user.service.SiteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SiteUserController {

    private final SiteUserService siteUserService;
    private static final Logger logger = LoggerFactory.getLogger(SiteUserController.class);

    @GetMapping("/signup")
    public String signup(SiteUserForm siteUserForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid SiteUserForm siteUserForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        if(!siteUserForm.getPassword1().equals(siteUserForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect"
                , "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            siteUserService.createSiteUser(siteUserForm);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: ", e);
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            logger.error("Unexpected error during signup: ", e);
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
}

package org.example.jtsb01.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.answer.service.AnswerService;
import org.example.jtsb01.global.exception.PasswordNotMatchException;
import org.example.jtsb01.mail.model.MailForm;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.PasswordForm;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.model.SiteUserForm;
import org.example.jtsb01.user.service.SiteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class SiteUserController {

    private final SiteUserService siteUserService;
    private static final Logger logger = LoggerFactory.getLogger(SiteUserController.class);
    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/signup")
    public String signup(SiteUserForm siteUserForm) {
        return "signup_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
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

    @GetMapping("/find/password")
    public String findPassword(MailForm mailForm) {
        return "mail_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/password")
    public String updatePassword(@PathVariable("id") Long id, PasswordForm passwordForm) {
        return "password_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/password")
    public String updatePassword(@PathVariable("id") Long id, @Valid PasswordForm passwordForm,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "password_form";
        }

        try {
            siteUserService.verifyPassword(id, passwordForm);
        } catch (PasswordNotMatchException e) {
            bindingResult.rejectValue("oldPassword", "passwordNotMatch", "기존 비밀번호가 일치하지 않습니다.");
            return "password_form";
        }
        if(!passwordForm.getNewPassword().equals(passwordForm.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "passwordInCorrect"
                , "2개의 패스워드가 일치하지 않습니다.");
            return "password_form";
        }

        siteUserService.updatePassword(id, passwordForm);
        return "redirect:/user/logout";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
        @RequestParam(value = "questionPage", defaultValue = "1") int questionPage,
        @RequestParam(value = "answerPage", defaultValue = "1") int answerPage,
        Model model) {

        SiteUserDto siteUser = siteUserService.getSiteUser(id);
        Page<QuestionDto> questionPaging = questionService.getListByAuthorId(id, questionPage);
        Page<AnswerDto> answerPaging = answerService.getList(id, answerPage);
//        questionService.getListByAuthorId(id);
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("questionPaging", questionPaging);
        model.addAttribute("answerPaging", answerPaging);
        return "user_detail";
    }
}

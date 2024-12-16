package org.example.jtsb01.mail.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.global.exception.EmailSendingException;
import org.example.jtsb01.mail.model.MailForm;
import org.example.jtsb01.mail.service.MailService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final SiteUserService siteUserService;

    @PostMapping("/send")
    public String emailSend(@Valid MailForm mailForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "mail_form";
        }

        try{
            String tempPassword = mailService.generateTempPassword();
            SiteUserDto siteUser = siteUserService.updateTempPassword(mailForm.getEmail(), tempPassword);
            mailService.sendTempPasswordEmail(siteUser.getEmail(), siteUser.getUsername(), tempPassword);
        } catch (DataNotFoundException e) {
            bindingResult.reject("emailNotExists", "존재하지 않는 이메일입니다.");
            return "mail_form";
        } catch (MessagingException e) {
            throw new EmailSendingException("이메일 전송에 실패했습니다.", e);
        }
        return "mail_send_success";
    }
}

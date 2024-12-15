package org.example.jtsb01.answer.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.entity.Answer;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.answer.service.AnswerService;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(@PathVariable("id") Long id, @Valid AnswerForm answerForm,
        BindingResult bindingResult, Model model, Principal principal) {

        QuestionDto question = questionService.getQuestion(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        logger.info("principal : {}", principal);
        if(principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String name = (String) oauth2User.getAttributes().get("name");

            SiteUserDto siteUser = siteUserService.getSiteUser(name);
            answerService.createAnswer(id, answerForm, siteUser);
        } else {
            SiteUserDto siteUser = siteUserService.getSiteUser(principal.getName());
            answerService.createAnswer(id, answerForm, siteUser);
        }

        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(AnswerForm answerForm, @PathVariable("id") Long id) {
        AnswerDto answer = answerService.getAnswer(id);
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
        @PathVariable("id") Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        AnswerDto answer = answerService.getAnswer(id);
        String username = getUsernameFromPrincipal(principal);
        checkUserPermission(username, answer.getAuthor().getUsername(), "수정");
        answerService.modifyAnswer(id, answerForm);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        AnswerDto answer = answerService.getAnswer(id);
        String username = getUsernameFromPrincipal(principal);
        checkUserPermission(username, answer.getAuthor().getUsername(), "삭제");
        answerService.deleteAnswer(id);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
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

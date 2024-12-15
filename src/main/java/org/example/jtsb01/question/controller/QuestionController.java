package org.example.jtsb01.question.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        Page<QuestionDto> paging = questionService.getList(page).map(QuestionDto::fromEntity);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, AnswerForm answerForm,
        @RequestParam(value = "page", defaultValue = "1") int page) {
        QuestionDto question = questionService.getQuestion(id, page);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult,
        Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        logger.info("principal : {}", principal);
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String name = (String) oauth2User.getAttributes().get("name");

            SiteUserDto siteUser = siteUserService.getSiteUser(name);
            questionService.createQuestion(questionForm, siteUser);
        } else {
            SiteUserDto siteUser = siteUserService.getSiteUser(principal.getName());
            questionService.createQuestion(questionForm, siteUser);
        }

        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(QuestionForm questionForm, @PathVariable("id") Long id,
        Principal principal) {
        QuestionDto question = questionService.getQuestion(id);

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid QuestionForm questionForm, BindingResult bindingResult,
        @PathVariable("id") Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        QuestionDto question = questionService.getQuestion(id);

        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String name = (String) oauth2User.getAttributes().get("name");
            if (!question.getAuthor().getUsername().equals(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
        } else {
            if (!question.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
        }

        questionService.modifyQuestion(id, questionForm);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        QuestionDto question = questionService.getQuestion(id);
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String name = (String) oauth2User.getAttributes().get("name");
            if (!question.getAuthor().getUsername().equals(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            }
        } else {
            if (!question.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            }
        }
        questionService.deleteQuestion(id);
        return "redirect:/";
    }
}

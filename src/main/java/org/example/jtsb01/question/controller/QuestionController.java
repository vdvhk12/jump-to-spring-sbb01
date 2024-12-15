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
@RequestMapping("/question")
public class QuestionController {

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
        SiteUserDto siteUser = siteUserService.getSiteUser(principal.getName());
        questionService.createQuestion(questionForm, siteUser);
        return "redirect:/question/list";
    }
}

package org.example.jtsb01.answer.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.answer.service.AnswerService;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(@PathVariable("id") Long id, @Valid AnswerForm answerForm,
        BindingResult bindingResult, Model model, Principal principal) {
        QuestionDto question = questionService.getQuestion(id);
        SiteUserDto siteUser = siteUserService.getSiteUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        answerService.createAnswer(id, answerForm, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}

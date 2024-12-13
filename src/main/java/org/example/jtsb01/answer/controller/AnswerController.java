package org.example.jtsb01.answer.controller;

import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.answer.service.AnswerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String create(@PathVariable("id") Long id, @ModelAttribute AnswerForm answerForm) {
        answerService.createAnswer(id, answerForm);
        return String.format("redirect:/question/detail/%s", id);
    }
}

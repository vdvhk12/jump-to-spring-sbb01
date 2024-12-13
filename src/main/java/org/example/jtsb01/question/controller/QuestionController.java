package org.example.jtsb01.question.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/question/list")
    public String list(Model model) {
        List<QuestionDto> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);
        return "question_list";
    }
}

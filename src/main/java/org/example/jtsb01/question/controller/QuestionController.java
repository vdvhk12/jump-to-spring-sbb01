package org.example.jtsb01.question.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.category.model.CategoryDto;
import org.example.jtsb01.category.service.CategoryService;
import org.example.jtsb01.comment.model.CommentForm;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
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

    private final QuestionService questionService;
    private final SiteUserService siteUserService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "category", defaultValue = "") Long categoryId,
        @RequestParam(value = "kw", defaultValue = "") String kw) {
        List<CategoryDto> categoryList = categoryService.getAllCategoriesWithQuestionList();

        Page<QuestionDto> paging;
        if (categoryId != null) {
            paging = questionService.getListByCategory(categoryId, page);
        } else {
            paging = questionService.getList(kw, page);
        }
        model.addAttribute("paging", paging);
        model.addAttribute("categoryList", categoryList);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id,
        Model model, AnswerForm answerForm, CommentForm commentForm,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "sort", defaultValue = "") String sort) {
        QuestionDto question = questionService.getQuestion(id, page, sort);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm, Model model) {
        List<CategoryDto> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult,
        Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        String username = getUsernameFromPrincipal(principal);
        SiteUserDto siteUser = siteUserService.getSiteUser(username);
        questionService.createQuestion(questionForm, siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(@PathVariable("id") Long id, QuestionForm questionForm, Model model) {
        List<CategoryDto> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        QuestionDto question = questionService.getQuestion(id);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        questionForm.setCategoryId(question.getCategory().getId());
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
        String username = getUsernameFromPrincipal(principal);
        checkUserPermission(username, question.getAuthor().getUsername(), "수정");
        questionService.modifyQuestion(id, questionForm);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        QuestionDto question = questionService.getQuestion(id);
        String username = getUsernameFromPrincipal(principal);
        checkUserPermission(username, question.getAuthor().getUsername(), "삭제");
        questionService.deleteQuestion(id);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(@PathVariable("id") Long id, Principal principal) {
        SiteUserDto siteUser = siteUserService.getSiteUser(getUsernameFromPrincipal(principal));
        questionService.voteQuestion(id, siteUser);
        return String.format("redirect:/question/detail/%s", id);
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

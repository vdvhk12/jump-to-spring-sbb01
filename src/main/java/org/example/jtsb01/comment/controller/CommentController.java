package org.example.jtsb01.comment.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.comment.model.CommentDto;
import org.example.jtsb01.comment.model.CommentForm;
import org.example.jtsb01.comment.service.CommentService;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.service.QuestionService;
import org.example.jtsb01.user.model.SiteUserDto;
import org.example.jtsb01.user.service.SiteUserService;
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
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final SiteUserService siteUserService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/create")
    public String createQuestionComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Model model, Principal principal) {
        QuestionDto question = questionService.getQuestion(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        String username = getUsernameFromPrincipal(principal);
        SiteUserDto siteUser = siteUserService.getSiteUser(username);
        commentService.createQuestionComment(id, commentForm, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyQuestionComment(@PathVariable("id") Long id, CommentForm commentForm,
        Model model, Principal principal) {
        CommentDto comment = commentService.getQuestionComment(id);
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyQuestionComment(@PathVariable("id") Long id, @Valid CommentForm commentForm,
        BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        CommentDto comment = commentService.getQuestionComment(id);
        String username = getUsernameFromPrincipal(principal);
        checkUserPermission(username, comment.getAuthor().getUsername(), "수정");
        commentService.modifyQuestionComment(id, commentForm);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }

    private String getUsernameFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            return (String) oauth2User.getAttributes().get("name");
        }
        return principal.getName();
    }
    private void checkUserPermission(String principalUsername, String targetUsername, String action) {
        if (!principalUsername.equals(targetUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, action + " 권한이 없습니다.");
        }
    }
}

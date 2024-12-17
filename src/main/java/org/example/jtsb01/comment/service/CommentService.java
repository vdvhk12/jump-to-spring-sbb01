package org.example.jtsb01.comment.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.comment.entity.Comment;
import org.example.jtsb01.comment.model.CommentForm;
import org.example.jtsb01.comment.repository.CommentRepository;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.example.jtsb01.user.model.SiteUserDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;

    public void createQuestionComment(Long questionId, CommentForm commentForm,
        SiteUserDto siteUserDto) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new DataNotFoundException("question not found"));
        commentRepository.save(Comment.builder()
            .content(commentForm.getContent())
            .createDate(LocalDateTime.now())
            .author(SiteUserDto.fromDto(siteUserDto))
            .question(question)
            .build());
    }

}

package org.example.jtsb01.question.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.category.model.CategoryDto;
import org.example.jtsb01.comment.model.CommentDto;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.user.model.SiteUserDto;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
public class QuestionDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private CategoryDto category;
    private Long answerCount;
    private SiteUserDto author;
    private Page<AnswerDto> answerList;
    private List<CommentDto> commentList;
    private Set<SiteUserDto> voter;

    //목록페이지
    public static QuestionDto fromEntity(Question question) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createDate(question.getCreateDate())
            .category(CategoryDto.fromEntityBasic(question.getCategory()))
            .answerCount((long) question.getAnswerList().size())
            .author(SiteUserDto.fromEntity(question.getAuthor()))
            .build();
    }

    //상세페이지
    public static QuestionDto fromEntity(Question question, Page<AnswerDto> answerPage) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createDate(question.getCreateDate())
            .category(CategoryDto.fromEntityBasic(question.getCategory()))
            .modifyDate(question.getModifyDate())
            .answerCount(answerPage.getTotalElements())
            .author(SiteUserDto.fromEntity(question.getAuthor()))
            .answerList(answerPage)
            .commentList(
                question.getCommentList().stream().map(CommentDto::questionCommentFromEntity)
                    .toList())
            .voter(question.getVoter().stream().map(SiteUserDto::fromEntity)
                .collect(Collectors.toSet()))
            .build();
    }
}

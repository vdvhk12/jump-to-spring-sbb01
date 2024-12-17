package org.example.jtsb01.comment.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.comment.entity.Comment;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.user.model.SiteUserDto;

@Getter
@Setter
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private SiteUserDto author;
    private QuestionDto question;
    private AnswerDto answer;
    private Set<SiteUserDto> voter;

    public static CommentDto questionCommentFromEntity(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createDate(comment.getCreateDate())
            .modifyDate(comment.getModifyDate())
            .author(SiteUserDto.fromEntity(comment.getAuthor()))
            .question(QuestionDto.builder()
                .id(comment.getQuestion().getId()).build())
            .voter(comment.getVoter().stream().map(SiteUserDto::fromEntity)
                .collect(Collectors.toSet()))
            .build();
    }

}

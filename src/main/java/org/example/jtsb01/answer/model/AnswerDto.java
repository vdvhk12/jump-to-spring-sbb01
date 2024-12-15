package org.example.jtsb01.answer.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.answer.entity.Answer;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.user.model.SiteUserDto;

@Getter
@Setter
@Builder
public class AnswerDto {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private SiteUserDto author;
    private QuestionDto question;

    public static AnswerDto fromEntity(Answer answer) {
        return AnswerDto.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .createDate(answer.getCreateDate())
            .author(SiteUserDto.fromEntity(answer.getAuthor()))
            .question(QuestionDto.builder()
                .id(answer.getQuestion().getId())
                .build())
            .build();
    }
}

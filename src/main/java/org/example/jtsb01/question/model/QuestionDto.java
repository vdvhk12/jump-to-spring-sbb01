package org.example.jtsb01.question.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.question.entity.Question;

@Getter
@Setter
@Builder
public class QuestionDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private List<AnswerDto> answerList;

    public static QuestionDto fromEntity(Question question) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createDate(question.getCreateDate())
            .answerList(question.getAnswerList().stream().map(AnswerDto::fromEntity).collect(
                Collectors.toList()))
            .build();
    }
}

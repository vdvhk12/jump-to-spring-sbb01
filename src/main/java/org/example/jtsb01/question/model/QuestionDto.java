package org.example.jtsb01.question.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.question.entity.Question;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
public class QuestionDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private Long answerCount;
    private Page<AnswerDto> answerList;

    //목록페이지
    public static QuestionDto fromEntity(Question question) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .createDate(question.getCreateDate())
            .build();
    }

    //상세페이지
    public static QuestionDto fromEntity(Question question, Page<AnswerDto> answerPage) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createDate(question.getCreateDate())
            .answerCount(answerPage.getTotalElements())
            .answerList(answerPage)
            .build();
    }
}

package org.example.jtsb01.question.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

    private Long categoryId;

    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 100)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}

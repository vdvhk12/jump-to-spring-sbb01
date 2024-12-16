package org.example.jtsb01.category.model;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.jtsb01.category.entity.Category;
import org.example.jtsb01.question.model.QuestionDto;

@Getter
@Setter
@Builder
public class CategoryDto {

    private Long id;
    private String categoryName;
    private QuestionDto question;
    private List<QuestionDto> questionList;

    public static CategoryDto fromEntityWithQuestionList(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .categoryName(category.getCategoryName())
            .questionList(category.getQuestionList().stream().map(QuestionDto::fromEntity).collect(
                Collectors.toList()))
            .build();
    }

    public static CategoryDto fromEntityBasic(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .categoryName(category.getCategoryName())
            .build();
    }
}

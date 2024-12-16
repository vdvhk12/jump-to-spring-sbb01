package org.example.jtsb01.category.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    @NotEmpty(message = "카테고리 명은 필수항목입니다.")
    private String categoryName;
}

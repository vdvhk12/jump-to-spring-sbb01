package org.example.jtsb01.category.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb01.category.entity.Category;
import org.example.jtsb01.category.model.CategoryForm;
import org.example.jtsb01.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(CategoryForm categoryForm) {
        categoryRepository.save(Category.builder()
            .categoryName(categoryForm.getCategoryName())
            .build());
    }
}

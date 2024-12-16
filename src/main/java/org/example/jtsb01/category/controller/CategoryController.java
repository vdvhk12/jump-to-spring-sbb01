package org.example.jtsb01.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.category.model.CategoryForm;
import org.example.jtsb01.category.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String create(CategoryForm categoryForm) {
        return "category_form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String create(@Valid CategoryForm categoryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "category_form";
        }
        categoryService.createCategory(categoryForm);
        return "redirect:/question/list";
    }
}

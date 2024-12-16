package org.example.jtsb01.category.repository;

import org.example.jtsb01.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

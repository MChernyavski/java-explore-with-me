package ru.practicum.category.service;

import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto patchCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto getCategoryById(long catId);

    List<CategoryDto> getAllCategories(int from, int size);
}

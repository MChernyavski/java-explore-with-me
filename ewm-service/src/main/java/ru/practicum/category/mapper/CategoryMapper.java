package ru.practicum.category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}

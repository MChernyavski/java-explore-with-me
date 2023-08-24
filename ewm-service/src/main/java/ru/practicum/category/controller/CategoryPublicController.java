package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.info("Getting information about category with id {}", catId);
        return categoryService.getCategoryById(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting categories");
        return categoryService.getAllCategories(from, size);
    }
}

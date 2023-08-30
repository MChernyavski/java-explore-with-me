package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        Category newCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Сategory with id {} doesn't exist " + catId));
        Category updatedCategory = CategoryMapper.toCategory(categoryDto);
        updatedCategory.setId(catId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Сategory with id {} doesn't exist " + catId));
        if (eventRepository.existsByCategory(category)) {
            throw new CategoryConflictException("Category isn't empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Сategory with id {} doesn't exist " + catId));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        return categoryRepository.findAll(pageRequest).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }
}

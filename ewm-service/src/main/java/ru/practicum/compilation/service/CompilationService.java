package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto);
}

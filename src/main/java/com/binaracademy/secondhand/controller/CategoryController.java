package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.CategoryDto;
import com.binaracademy.secondhand.dto.RestDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.service.CategoryService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    @PostMapping("/category/save")
    public ResponseEntity<RestDto> saveCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getCategoryName() != null) {
            Category result = categoryService.saveCategory(categoryDto);
            log.info("Success - saving category");
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/category/save").toUriString());
            return ResponseEntity.created(uri).body(new RestDto(201, "Category created", result));
        }

        log.info("Failed - saving category");
        return ResponseEntity.badRequest().body(new RestDto(400, "Category name cannot be empty", ""));
    }

    @GetMapping("/categories")
    public ResponseEntity<RestDto> getAllCategories() {
        return ResponseEntity.ok(new RestDto(200, "ok", categoryService.getAllCategories()));
    }
}

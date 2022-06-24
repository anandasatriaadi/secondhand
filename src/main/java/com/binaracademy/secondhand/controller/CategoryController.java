package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.CategoryDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.service.CategoryService;
import java.net.URI;
import java.util.List;
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
    public ResponseEntity<Object> saveCategory(@RequestBody CategoryDto categoryDto) {
        Category result = null;
        try {
            result = categoryService.saveCategory(categoryDto);
            log.info("Success - saving category");
        } catch (Exception e) {
            log.info("Failed - saving category");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/category/save").toUriString());
        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}

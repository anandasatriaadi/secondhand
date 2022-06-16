package com.binaracademy.secondhand.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binaracademy.secondhand.dto.CategoryDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {
	private final CategoryService categoryService;
	
	@PostMapping("add_category")
	public ResponseEntity<Category> saveCategory(@RequestBody CategoryDto categoryDto) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/add_category").toUriString());
		return ResponseEntity.created(uri).body(categoryService.saveCategory(categoryDto));
	}
	
	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}
}

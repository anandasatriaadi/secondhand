package com.binaracademy.secondhand.service;

import java.util.List;

import com.binaracademy.secondhand.model.Category;

public interface CategoryService {
	Category saveCategory(Category category);
	Category getCategory(Long id);
	List<Category> getAllCategories();
}

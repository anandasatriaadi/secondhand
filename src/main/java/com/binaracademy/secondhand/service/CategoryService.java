package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.CategoryUploadDto;
import com.binaracademy.secondhand.model.Category;
import java.util.List;

public interface CategoryService {
    Category saveCategory(CategoryUploadDto categoryDto);
    Category getCategory(Long id);
    List<Category> getAllCategories();
}

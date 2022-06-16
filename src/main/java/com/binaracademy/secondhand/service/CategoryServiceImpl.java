package com.binaracademy.secondhand.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private final CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		log.info("Saving Category");
		return categoryRepository.save(category);
	}

	@Override
	public Category getCategory(Long id) {
		log.info("Getting Category");
		Optional<Category> res = categoryRepository.findById(id);
		if(res.isPresent()) {
			return res.get();
		} else {
			return null;
		}
	}

	@Override
	public List<Category> getAllCategories() {
		log.info("Getting All Categories");
		return categoryRepository.findAll();
	}
}
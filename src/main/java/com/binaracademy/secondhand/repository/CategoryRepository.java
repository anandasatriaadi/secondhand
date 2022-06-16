package com.binaracademy.secondhand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.binaracademy.secondhand.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

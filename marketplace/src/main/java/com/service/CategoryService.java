package com.service;

import java.util.ArrayList;
import java.util.Optional;

import com.entity.Category;
import com.exceptions.CategoryDuplicateException;

public interface CategoryService {
    ArrayList<Category> getCategories();
    Optional<Category> getCategoryById(int categoryId);
    Category createCategory(int newCategoryId, String description) throws CategoryDuplicateException;
}

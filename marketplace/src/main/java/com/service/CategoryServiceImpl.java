package com.service;

import java.util.ArrayList;
import java.util.Optional;

import com.entity.Category;
import com.exceptions.CategoryDuplicateException;
import com.repository.CategoryRepository;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl() {
        categoryRepository = new CategoryRepository();
    }

    public ArrayList<Category> getCategories() {
        return categoryRepository.getCategories();
    }

    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    public Category createCategory(int newCategoryId, String description) throws CategoryDuplicateException {
        ArrayList<Category> categories = categoryRepository.getCategories();
        if (categories.stream().anyMatch(
                category -> category.getId() == newCategoryId && category.getDescription().equals(description)))
            throw new CategoryDuplicateException();
        return categoryRepository.createCategory(newCategoryId, description);
    }
}

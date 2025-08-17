package tpo.e_commerce.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tpo.e_commerce.entity.Category;
import tpo.e_commerce.exceptions.CategoryDuplicateException;
import tpo.e_commerce.exceptions.CategoryNonexistentException;
import tpo.e_commerce.repository.CategoryRepository;

public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService() {
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

    public void deleteCategory(int categoryId) {
        ArrayList<Category> categories = categoryRepository.getCategories();
        categories.removeIf(category -> category.getId() == categoryId);
    }

    public Category updateCategory(int categoryId, String newDescription) 
            throws CategoryNonexistentException {
        // Verificar que la categoría a actualizar exista
        Optional<Category> categoryToUpdate = getCategoryById(categoryId);
        if (!categoryToUpdate.isPresent()) {
            throw new CategoryNonexistentException();
        }

        // Actualizar la descripción
        Category category = categoryToUpdate.get();
        category.setDescription(newDescription);
        return category;
    }
    
}


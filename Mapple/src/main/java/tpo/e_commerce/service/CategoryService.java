package tpo.e_commerce.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tpo.e_commerce.entity.Category;
import tpo.e_commerce.exceptions.CategoryDuplicateException;
import tpo.e_commerce.exceptions.CategoryNonexistentException;
import tpo.e_commerce.repository.CategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(String description) throws CategoryDuplicateException {
        Category newCategory = Category.builder()
                .description(description)
                .build();
        return categoryRepository.save(newCategory);
    }

    public void deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public Category updateCategory(UUID categoryId, String newDescription) 
            throws CategoryNonexistentException {
        Optional<Category> categoryToUpdate = getCategoryById(categoryId);
        if (categoryToUpdate.isEmpty()) {
            throw new CategoryNonexistentException();
        }

        Category category = categoryToUpdate.get();
        category.setDescription(newDescription);
        return categoryRepository.save(category);
    }
}


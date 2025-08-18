package tpo.e_commerce.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tpo.e_commerce.entity.Category;
import tpo.e_commerce.entity.dto.CategoryRequest;
import tpo.e_commerce.exceptions.CategoryDuplicateException;
import tpo.e_commerce.exceptions.CategoryNonexistentException;
import tpo.e_commerce.service.CategoryService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("categories")
public class CategoriesController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID categoryId) throws CategoryNonexistentException {
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        if (category.isEmpty()) {
            throw new CategoryNonexistentException();
        }
        return ResponseEntity.ok(category.get());
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody CategoryRequest categoryRequest)
            throws CategoryDuplicateException {
        Category result = categoryService.createCategory(categoryRequest.getDescription());
        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable UUID categoryId) 
            throws CategoryNonexistentException {
        Optional<Category> category = categoryService.getCategoryById(categoryId);
        if (category.isEmpty()) {
            throw new CategoryNonexistentException();
        }
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().body(Map.of("message", "Category: " + category.get().getDescription() + " with ID: " + categoryId + " was successfully deleted"));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Object> updateCategory(@PathVariable UUID categoryId, @RequestBody CategoryRequest categoryRequest) 
            throws CategoryNonexistentException {
        Category updatedCategory = categoryService.updateCategory(categoryId, categoryRequest.getDescription());
        return ResponseEntity.ok(updatedCategory);
    }
}

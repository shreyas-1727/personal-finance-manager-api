package com.finance.manager.service;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getCategoriesForUser(Long userId) {
        return categoryRepository.findAllAvailableForUser(userId);
    }

    public Category createCustomCategory(CategoryRequest request, Long userId) {
        // Check for duplicates
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new IllegalArgumentException("Category name already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = new Category(
                request.getName(),
                request.getType().toUpperCase(),
                true, // isCustom is always true for user-created categories
                user
        );

        return categoryRepository.save(category);
    }

    public void deleteCustomCategory(String name, Long userId) {
        Category category = categoryRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!category.getIsCustom()) {
            throw new IllegalArgumentException("Cannot delete default system categories");
        }

        // Note: We will add the logic to check for linked transactions later when we build the Transaction module.
        categoryRepository.delete(category);
    }
}
package com.finance.manager.controller;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Helper method to check authentication
    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("Unauthorized access");
        }
        return userId;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        List<Category> categories = categoryService.getCategoriesForUser(userId);

        // Format the response exactly as requested in the assignment PDF
        List<Map<String, Object>> formattedCategories = categories.stream().map(cat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", cat.getName());
            map.put("type", cat.getType());
            map.put("isCustom", cat.getIsCustom());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", formattedCategories);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody CategoryRequest request, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        Category savedCategory = categoryService.createCustomCategory(request, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("name", savedCategory.getName());
        response.put("type", savedCategory.getType());
        response.put("isCustom", savedCategory.getIsCustom());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable String name, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        categoryService.deleteCustomCategory(name, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category deleted successfully");
        
        return ResponseEntity.ok(response);
    }
}
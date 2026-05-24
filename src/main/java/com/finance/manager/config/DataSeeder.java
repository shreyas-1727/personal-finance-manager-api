package com.finance.manager.config;

import com.finance.manager.entity.Category;
import com.finance.manager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only run the seeder if the categories table is empty
        if (categoryRepository.count() == 0) {
            
            List<Category> defaultCategories = Arrays.asList(
                new Category("Salary", "INCOME", false, null),
                new Category("Food", "EXPENSE", false, null),
                new Category("Rent", "EXPENSE", false, null),
                new Category("Transportation", "EXPENSE", false, null),
                new Category("Entertainment", "EXPENSE", false, null),
                new Category("Healthcare", "EXPENSE", false, null),
                new Category("Utilities", "EXPENSE", false, null)
            );

            categoryRepository.saveAll(defaultCategories);
            System.out.println("Database seeded with default categories.");
        }
    }
}
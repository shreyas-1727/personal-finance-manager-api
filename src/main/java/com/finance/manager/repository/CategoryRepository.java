package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Fetches all system defaults PLUS the specific user's custom categories
    @Query("SELECT c FROM Category c WHERE c.isCustom = false OR c.user.id = :userId")
    List<Category> findAllAvailableForUser(@Param("userId") Long userId);

    // Used to check if a user already has a custom category with this name
    boolean existsByNameAndUserId(String name, Long userId);

    // Used to find a specific custom category to delete it
    Optional<Category> findByNameAndUserId(String name, Long userId);
}
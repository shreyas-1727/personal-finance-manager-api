package com.finance.manager.repository;

import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA magically implements this query based on the method name
    Optional<User> findByUsername(String username);
    
    // Useful for checking if an email is already registered
    Boolean existsByUsername(String username);
}
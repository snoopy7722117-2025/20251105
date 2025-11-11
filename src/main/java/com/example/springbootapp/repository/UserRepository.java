package com.example.springbootapp.repository;

import com.example.springbootapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository Interface
 * In Struts 1.0, data access was typically done through custom DAO classes
 * Spring Data JPA provides automatic implementation of common CRUD operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
}

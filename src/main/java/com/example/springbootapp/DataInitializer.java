package com.example.springbootapp;

import com.example.springbootapp.model.User;
import com.example.springbootapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data Initializer
 * Populates the database with sample data on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Add sample users
        User user1 = new User("johndoe", "john.doe@example.com", "John Doe");
        User user2 = new User("janedoe", "jane.doe@example.com", "Jane Doe");
        User user3 = new User("bobsmith", "bob.smith@example.com", "Bob Smith");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("Sample data initialized: 3 users created");
    }
}

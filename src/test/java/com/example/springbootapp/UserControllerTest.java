package com.example.springbootapp;

import com.example.springbootapp.controller.UserController;
import com.example.springbootapp.model.User;
import com.example.springbootapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 * Demonstrates modern Spring Boot testing practices
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testListUsers() throws Exception {
        // Prepare test data
        User user1 = new User("johndoe", "john@example.com", "John Doe");
        user1.setId(1L);
        User user2 = new User("janedoe", "jane@example.com", "Jane Doe");
        user2.setId(2L);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Test
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testShowEditForm() throws Exception {
        User user = new User("johndoe", "john@example.com", "John Doe");
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testCreateUserWithValidData() throws Exception {
        User user = new User("johndoe", "john@example.com", "John Doe");
        
        when(userService.existsByUsername("johndoe")).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                .param("username", "johndoe")
                .param("email", "john@example.com")
                .param("name", "John Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testCreateUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/users")
                .param("username", "")
                .param("email", "invalid-email")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeHasFieldErrors("user", "username", "email", "name"));
    }
}

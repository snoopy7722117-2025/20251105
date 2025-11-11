package com.example.springbootapp.controller;

import com.example.springbootapp.model.User;
import com.example.springbootapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User Controller - replaces Struts Action classes
 * 
 * In Struts 1.0, you would have:
 * - Action class with execute() method
 * - ActionMapping in struts-config.xml
 * - ActionForm for data binding
 * 
 * In Spring Boot:
 * - @Controller annotation replaces Action class registration
 * - @RequestMapping replaces struts-config.xml mappings
 * - @Valid and Model attributes replace ActionForm
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Display list of all users
     * Struts equivalent: forward name="success" in struts-config.xml
     */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    /**
     * Show form to create new user
     * Struts equivalent: forward name="input" in struts-config.xml
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    /**
     * Handle user creation form submission
     * Struts equivalent: Action.execute() method with ActionForm validation
     */
    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "users/form";
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", 
                "Username already exists");
            return "users/form";
        }

        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", 
            "User created successfully!");
        return "redirect:/users";
    }

    /**
     * Show form to edit existing user
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "users/form";
    }

    /**
     * Handle user update form submission
     */
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            user.setId(id);
            return "users/form";
        }

        user.setId(id);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", 
            "User updated successfully!");
        return "redirect:/users";
    }

    /**
     * Delete a user
     */
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, 
                           RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", 
            "User deleted successfully!");
        return "redirect:/users";
    }
}

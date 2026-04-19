package com.grocery.controller;

import com.grocery.dto.UserDTO;
import com.grocery.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Create User
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {

        logger.info("Creating user with email: {}", userDTO.getEmail());

        UserDTO createdUser = userService.createUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {

        logger.info("Fetching user with id: {}", id);

        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Get All Users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        logger.info("Fetching all users");

        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Update User
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {

        logger.info("Updating user with id: {}", id);

        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    // Delete User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        logger.info("Deleting user with id: {}", id);

        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    // Count Users
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {

        logger.info("Fetching user count");

        return ResponseEntity.ok(userService.getUserCount());
    }
}
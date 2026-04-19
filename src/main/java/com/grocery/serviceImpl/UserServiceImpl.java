package com.grocery.serviceImpl;

import com.grocery.dto.UserDTO;
import com.grocery.exception.UserException;
import com.grocery.model.User;
import com.grocery.repository.UserRepository;
import com.grocery.security.CustomUserDetails;
import com.grocery.service.UserService;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    // Create a new user
    @Override
    public UserDTO createUser(UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserException("User already exists with this email");
        }

        User user = modelMapper.map(userDTO, User.class);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 🔥 IMPORTANT

        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }


    // Get a user by ID
    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        // Convert the entity to DTO and return it
        return modelMapper.map(user, UserDTO.class);
    }


    // Get all users
    @Override
    public List<UserDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        // Convert the list of entities to list of DTOs and return
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    // Update an existing user by ID
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        // Updating the fields of the existing entity/User
        modelMapper.map(userDTO, existingUser);

        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDTO.class);
    }


    // Delete a user by ID
    @Override
    public String deleteUserById(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with ID: " + id));

        // Delete the user from the database
        userRepository.delete(existingUser);

        return "User deleted successfully!";
    }

    @Override
    public long getUserCount() throws ServiceException {

        try {
            return userRepository.count();
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to get user count", e);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        logger.info("User authenticated: {}", username);

        return new CustomUserDetails(user);
    }



}

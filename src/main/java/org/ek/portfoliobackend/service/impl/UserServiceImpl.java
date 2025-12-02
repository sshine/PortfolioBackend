package org.ek.portfoliobackend.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.ek.portfoliobackend.dto.request.CreateUserRequest;
import org.ek.portfoliobackend.dto.request.UpdateUserRequest;
import org.ek.portfoliobackend.dto.response.UserResponse;
import org.ek.portfoliobackend.model.User;
import org.ek.portfoliobackend.repository.UserRepository;
import org.ek.portfoliobackend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
    // check if username or email already exists
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new IllegalArgumentException("Username already exists");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("Email already exists");
    }

    // create new user with hashed password
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());

    User savedUser = userRepository.save(user);
    return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // update fields if present:
        // username
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        // email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        // password
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        // role
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            user.setRole(request.getRole());
        }
        // save updated user
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
    if(!userRepository.existsById(id)) {
        throw new IllegalArgumentException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

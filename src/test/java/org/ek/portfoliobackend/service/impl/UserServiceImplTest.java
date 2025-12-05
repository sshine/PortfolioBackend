package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateUserRequest;
import org.ek.portfoliobackend.dto.request.UpdateUserRequest;
import org.ek.portfoliobackend.dto.response.UserResponse;
import org.ek.portfoliobackend.model.User;
import org.ek.portfoliobackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // Create User
    @Test
    void createUser_shouldCreateUserWithHashedPassword() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Jens64", "admin@test.io",
                "Hansen2024", "ROLE_ADMIN"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("Jens64");
        savedUser.setEmail("admin@test.io");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole("ROLE_ADMIN");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("Jens64");
        assertThat(response.getEmail()).isEqualTo("admin@test.io");
        assertThat(response.getRole()).isEqualTo("ROLE_ADMIN");
        verify(userRepository).save(any(User.class));
    }

    // Create User - Username exists
    @Test
    void createUser_shouldThrowException_whenUsernameExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Jens64", "jensen@pizzabar.dk",
                "Hansen2024", "ROLE_ADMIN"
        );

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(request)) //by running this method
                .isInstanceOf(IllegalArgumentException.class) // expect this exception
                .hasMessage("Username already exists"); // with this message

        verify(userRepository, never()).save(any(User.class)); // ensure save is never called
    }

    // Create User - Email exists
    @Test
    void createUser_shouldThrowException_whenEmailExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Jens64", "jensen@pizzabar.dk",
                "Hansen2024", "ROLE_ADMIN"
        );

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(request)) //by running this method
                .isInstanceOf(IllegalArgumentException.class) // expect this exception
                .hasMessage("Email already exists"); // with this message

        verify(userRepository, never()).save(any(User.class)); // ensure save is never called
    }

    // Get User by ID
    @Test
    void getUserById_shouldReturnUser_whenExists() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("Jens64");
        user.setEmail("hansens@bandeland.dk");
        user.setRole("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("Jens64");
    }

    // Get User by ID - Not found
    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(1L)) //by running this method
                .isInstanceOf(IllegalArgumentException.class) // expect this exception
                .hasMessage("User not found with id: 1"); // with this message
    }

    // Get All Users
    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("Jens64");
        user1.setEmail("email@admin.io");
        user1.setRole("ROLE_ADMIN");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Anna88");
        user2.setEmail("annas@email.dk");
        user2.setRole("ROLE_SALES");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserResponse> responses = userService.getAllUsers();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUsername()).isEqualTo("Jens64");
        assertThat(responses.get(1).getUsername()).isEqualTo("Anna88");
    }

    // Update User
    @Test
    void updateUser_shouldUpdateUsername() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("OldName");
        existingUser.setEmail("old@email.io");
        existingUser.setPassword("hashedPassword");
        existingUser.setRole("ROLE_ADMIN");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("NewName")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        UserResponse response = userService.updateUser(1L, request);

        // Assert
        assertThat(response.getUsername()).isEqualTo("NewName");
        verify(userRepository).save(any(User.class));
    }

    // Update User - Not found
    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(1L, request)) //by running this method
                .isInstanceOf(IllegalArgumentException.class) // expect this exception
                .hasMessage("User not found with id: 1"); // with this message
    }

    // Delete User
    @Test
    void deleteUser_shouldDeleteUser_whenExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    // Delete User - Not found
    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(1L)) //by running this method
                .isInstanceOf(IllegalArgumentException.class) // expect this exception
                .hasMessage("User not found with id: 1"); // with this message

        verify(userRepository, never()).deleteById(1L);
    }
}

package org.ek.portfoliobackend.controller;

import tools.jackson.databind.ObjectMapper;
import org.ek.portfoliobackend.dto.request.CreateUserRequest;
import org.ek.portfoliobackend.dto.request.UpdateUserRequest;
import org.ek.portfoliobackend.dto.response.UserResponse;
import org.ek.portfoliobackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_shouldReturn201() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "admin",
                "admin@test.dk",
                "password123",
                "ROLE_ADMIN"
        );

        UserResponse response = new UserResponse(1L, "admin", "admin@test.dk", "ROLE_ADMIN");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.dk"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void getUserById_shouldReturn200() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(1L, "admin", "admin@test.dk", "ROLE_ADMIN");

        when(userService.getUserById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void getAllUsers_shouldReturn200() throws Exception {
        // Arrange
        UserResponse user1 = new UserResponse(1L, "admin", "admin@test.dk", "ROLE_ADMIN");
        UserResponse user2 = new UserResponse(2L, "sales", "sales@test.dk", "ROLE_SALES");

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].username").value("sales"));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updatedAdmin");

        UserResponse response = new UserResponse(1L, "updatedAdmin", "admin@test.dk", "ROLE_ADMIN");

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedAdmin"));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
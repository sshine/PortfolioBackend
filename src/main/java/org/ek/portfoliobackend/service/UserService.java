package org.ek.portfoliobackend.service;

import org.ek.portfoliobackend.dto.request.CreateUserRequest;
import org.ek.portfoliobackend.dto.request.UpdateUserRequest;
import org.ek.portfoliobackend.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    //create new user
    UserResponse createUser(CreateUserRequest request);

    //get user by id
    UserResponse getUserById(Long id);

    //get all users
    List<UserResponse> getAllUsers();

    //update existing user
    UserResponse updateUser(Long id, UpdateUserRequest request);

    //delete user by id
    void deleteUser(Long id);
}

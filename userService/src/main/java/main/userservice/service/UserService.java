package main.userservice.service;

import main.userservice.dto.UserCreateDto;
import main.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto dto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto deleteUser(Long id);
    boolean existsById(Long id);
}
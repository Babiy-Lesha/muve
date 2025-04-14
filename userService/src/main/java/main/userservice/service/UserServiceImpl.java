package main.userservice.service;

import main.userservice.dto.UserCreateDto;
import main.userservice.dto.UserDto;
import main.userservice.entity.User;
import main.userservice.exception.ResourceNotFoundException;
import main.userservice.mapper.UserMapper;
import main.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        // Проверка на существование пользователя с таким же именем
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Пользователь с именем " + dto.getUsername() + " уже существует");
        }

        User user = userMapper.toEntityWithEncodedPassword(dto, passwordEncoder);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + id));

        UserDto userDto = userMapper.toDto(user);

        userRepository.deleteById(id);
        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
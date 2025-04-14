package main.userservice.mapper;

import main.userservice.dto.UserCreateDto;
import main.userservice.dto.UserDto;
import main.userservice.entity.Role;
import main.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateDto dto);

    default User toEntityWithEncodedPassword(UserCreateDto dto, PasswordEncoder passwordEncoder) {
        User user = toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            try {
                user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.USER);
            }
        } else {
            user.setRole(Role.USER);
        }

        return user;
    }
}
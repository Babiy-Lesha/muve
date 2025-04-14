package main.userservice.mapper;

import main.userservice.dto.UserMovieDto;
import main.userservice.entity.User;
import main.userservice.entity.UserMovie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMovieMapper {

    UserMovieMapper INSTANCE = Mappers.getMapper(UserMovieMapper.class);

    @Mapping(target = "userId", source = "user.id")
    UserMovieDto toDto(UserMovie userMovie);

    List<UserMovieDto> toDtoList(List<UserMovie> userMovies);

    default User userIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }

        User user = new User();
        user.setId(userId);
        return user;
    }
}
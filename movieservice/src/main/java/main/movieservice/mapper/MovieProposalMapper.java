package main.movieservice.mapper;

import main.movieservice.dto.MovieCreateDto;
import main.movieservice.dto.MovieProposalDto;
import main.movieservice.entity.Movie;
import main.movieservice.entity.MovieProposal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieProposalMapper {

    MovieProposalMapper INSTANCE = Mappers.getMapper(MovieProposalMapper.class);

    MovieProposalDto toDto(MovieProposal movieProposal);

    List<MovieProposalDto> toDtoList(List<MovieProposal> movieProposals);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "adminComment", ignore = true)
    MovieProposal toEntity(MovieProposalDto dto);

    MovieCreateDto proposalToMovieCreateDto(MovieProposal proposal);
}
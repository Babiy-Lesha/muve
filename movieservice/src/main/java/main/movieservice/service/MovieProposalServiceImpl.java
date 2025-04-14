package main.movieservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.movieservice.client.UserServiceClient;
import main.movieservice.dto.MovieCreateDto;
import main.movieservice.dto.MovieProposalDto;
import main.movieservice.entity.MovieProposal;
import main.movieservice.entity.ProposalStatus;
import main.movieservice.exception.ResourceNotFoundException;
import main.movieservice.mapper.MovieProposalMapper;
import main.movieservice.repository.MovieProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieProposalServiceImpl implements MovieProposalService {

    private final MovieProposalRepository movieProposalRepository;
    private final MovieProposalMapper movieProposalMapper;
    private final MovieService movieService;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public MovieProposalDto createProposal(MovieProposalDto dto) {
        Long userId = dto.getUserId();
        // Проверка существования пользователя через User Service
        if (userId != null) {
            boolean userExists = userServiceClient.checkUserExists(dto.getUserId());
            if (!userExists) {
                throw new ResourceNotFoundException("Пользователь не найден с id: " + userId);
            }
        }

        MovieProposal proposal = movieProposalMapper.toEntity(dto);
        proposal.setStatus(ProposalStatus.PENDING);
        MovieProposal savedProposal = movieProposalRepository.save(proposal);
        return movieProposalMapper.toDto(savedProposal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieProposalDto> getAllProposals() {
        List<MovieProposal> proposals = movieProposalRepository.findAll();
        return movieProposalMapper.toDtoList(proposals);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieProposalDto> getProposalsByStatus(ProposalStatus status) {
        List<MovieProposal> proposals = movieProposalRepository.findByStatus(status);
        return movieProposalMapper.toDtoList(proposals);
    }

    @Override
    @Transactional
    public MovieProposalDto approveProposal(Long id, String adminComment) {
        MovieProposal proposal = movieProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Предложение не найдено с id: " + id));

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new IllegalArgumentException("Можно обработать только предложения в статусе PENDING");
        }

        // Создаем фильм из предложения
        MovieCreateDto movieCreateDto = movieProposalMapper.proposalToMovieCreateDto(proposal);
        movieService.createMovie(movieCreateDto);

        // Обновляем статус предложения
        proposal.setStatus(ProposalStatus.APPROVED);
        proposal.setAdminComment(adminComment);
        MovieProposal updatedProposal = movieProposalRepository.save(proposal);

        return movieProposalMapper.toDto(updatedProposal);
    }

    @Override
    @Transactional
    public MovieProposalDto rejectProposal(Long id, String adminComment) {
        MovieProposal proposal = movieProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Предложение не найдено с id: " + id));

        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new IllegalArgumentException("Можно обработать только предложения в статусе PENDING");
        }

        proposal.setStatus(ProposalStatus.REJECTED);
        proposal.setAdminComment(adminComment);

        MovieProposal updatedProposal = movieProposalRepository.save(proposal);
        return movieProposalMapper.toDto(updatedProposal);
    }
}
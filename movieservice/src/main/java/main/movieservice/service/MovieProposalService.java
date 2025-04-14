package main.movieservice.service;

import main.movieservice.dto.MovieProposalDto;
import main.movieservice.entity.ProposalStatus;

import java.util.List;

public interface MovieProposalService {
    MovieProposalDto createProposal(MovieProposalDto dto);
    List<MovieProposalDto> getAllProposals();
    List<MovieProposalDto> getProposalsByStatus(ProposalStatus status);
    MovieProposalDto approveProposal(Long id, String adminComment);
    MovieProposalDto rejectProposal(Long id, String adminComment);
}
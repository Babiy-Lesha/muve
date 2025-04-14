package main.movieservice.repository;

import main.movieservice.entity.MovieProposal;
import main.movieservice.entity.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieProposalRepository extends JpaRepository<MovieProposal, Long> {
    List<MovieProposal> findByStatus(ProposalStatus status);
}
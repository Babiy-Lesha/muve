package main.vaadinui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.MovieProposalDto;
import main.vaadinui.exception.ApiException;
import main.vaadinui.security.SecurityService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieProposalService {

    private final RestClient restClient;
    private final SecurityService securityService;

    public List<MovieProposalDto> getAllProposals() {
        try {
            return restClient.get()
                    .uri("/api/proposals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MovieProposalDto>>() {});
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при получении предложений фильмов", e);
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw ApiException.forbidden();
            }
            throw ApiException.serverError("Ошибка при получении предложений фильмов: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении предложений фильмов", e);
            return Collections.emptyList();
        }
    }

    public MovieProposalDto createProposal(MovieProposalDto proposalDto) {
        try {
            // Устанавливаем ID текущего пользователя
            proposalDto.setUserId(securityService.getCurrentUserId());

            return restClient.post()
                    .uri("/api/proposals")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(proposalDto)
                    .retrieve()
                    .body(MovieProposalDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при создании предложения фильма", e);
            throw ApiException.serverError("Ошибка при создании предложения фильма: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании предложения фильма", e);
            throw ApiException.serverError("Неожиданная ошибка при создании предложения фильма: " + e.getMessage());
        }
    }

    public MovieProposalDto approveProposal(Long id, String adminComment) {
        try {
            return restClient.post()
                    .uri("/api/proposals/{id}/approve?adminComment={comment}", id, adminComment)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(MovieProposalDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при одобрении предложения фильма", e);
            throw ApiException.serverError("Ошибка при одобрении предложения фильма: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при одобрении предложения фильма", e);
            throw ApiException.serverError("Неожиданная ошибка при одобрении предложения фильма: " + e.getMessage());
        }
    }

    public MovieProposalDto rejectProposal(Long id, String adminComment) {
        try {
            return restClient.post()
                    .uri("/api/proposals/{id}/reject?adminComment={comment}", id, adminComment)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + securityService.getCurrentUser().getToken())
                    .retrieve()
                    .body(MovieProposalDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Ошибка при отклонении предложения фильма", e);
            throw ApiException.serverError("Ошибка при отклонении предложения фильма: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отклонении предложения фильма", e);
            throw ApiException.serverError("Неожиданная ошибка при отклонении предложения фильма: " + e.getMessage());
        }
    }
}
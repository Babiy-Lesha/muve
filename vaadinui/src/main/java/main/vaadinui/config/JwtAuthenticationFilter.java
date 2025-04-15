package main.vaadinui.config;

import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthResponse;
import main.vaadinui.security.SecurityService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        AuthResponse currentUser = securityService.getCurrentUser();

        if (currentUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Устанавливаем аутентификацию из SecurityService для пользователя: {}", currentUser.getUsername());

            // Создаем список ролей
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + currentUser.getRole())
            );

            // Создаем аутентификацию
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    currentUser.getUsername(),
                    null, // Пароль уже проверен, поэтому null
                    authorities
            );

            // Устанавливаем токен в details
            authToken.setDetails(currentUser.getToken());

            // Устанавливаем аутентификацию в контекст
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Аутентификация успешно установлена для пользователя: {}", currentUser.getUsername());
        }

        filterChain.doFilter(request, response);
    }
}
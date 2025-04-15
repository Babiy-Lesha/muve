package main.userservice.service;

import main.userservice.dto.AuthRequest;
import main.userservice.dto.AuthResponse;
import main.userservice.entity.User;
import main.userservice.repository.UserRepository;
import main.userservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        // 1. Получаем пользователя из базы данных
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Неверные учетные данные"));

        // 2. Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверные учетные данные");
        }

        // 3. Загружаем UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // 4. Генерируем токен
        String token = jwtTokenProvider.generateToken(userDetails);

        // 5. Создаем и возвращаем ответ
        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
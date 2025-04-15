package main.vaadinui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthResponse;
import main.vaadinui.dto.UserDto;
import main.vaadinui.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";
    private final UserService userService;

    @Getter
    private AuthResponse currentUser;
    private Long userId;

    public void setCurrentUser(AuthResponse user) {
        this.currentUser = user;

        if (user != null) {
            // Устанавливаем аутентификацию в Spring Security
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Установлена аутентификация для пользователя: {}", user.getUsername());

            // Получаем реальный ID пользователя от API
            try {
                UserDto userDto = userService.getUserByUsername(user.getUsername());
                if (userDto != null) {
                    this.userId = userDto.getId();
                    log.info("Получен ID пользователя: {}", this.userId);
                }
            } catch (Exception e) {
                log.error("Ошибка при получении данных пользователя", e);
                // Используем временный ID на основе имени
                this.userId = (long)user.getUsername().hashCode();
                log.info("Установлен временный ID пользователя: {}", this.userId);
            }
        } else {
            SecurityContextHolder.clearContext();
            this.userId = null;
            log.info("Аутентификация очищена");
        }
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public Long getCurrentUserId() {
        if (userId != null) {
            return userId;
        }

        if (currentUser != null) {
            // Пробуем получить ID через API, если еще не получен
            try {
                UserDto userDto = userService.getUserByUsername(currentUser.getUsername());
                if (userDto != null) {
                    this.userId = userDto.getId();
                    log.info("Получен ID пользователя: {}", this.userId);
                    return this.userId;
                }
            } catch (Exception e) {
                log.error("Ошибка при получении данных пользователя", e);
            }

            // Если не получилось, используем временный ID
            return (long)currentUser.getUsername().hashCode();
        }

        return null;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        setCurrentUser(null);
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
    }
}
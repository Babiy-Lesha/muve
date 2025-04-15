package main.vaadinui.security;

import com.vaadin.flow.component.UI;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import main.vaadinui.dto.AuthResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";

    @Getter
    private AuthResponse currentUser;

    @Getter @Setter
    private Long userId;

    public void setCurrentUser(AuthResponse user) {
        this.currentUser = user;

        if (user != null) {
            // Устанавливаем аутентификацию в Spring Security
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Установлена аутентификация для пользователя: {}", user.getUsername());

            // Устанавливаем временный ID пользователя
            this.userId = (long)user.getUsername().hashCode();
            log.info("Установлен временный ID пользователя: {}", this.userId);
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
        return userId;
    }

    public void logout() {
        UI ui = UI.getCurrent();
        SecurityContextHolder.clearContext();
        setCurrentUser(null);

        // Очищаем localStorage
        if (ui != null) {
            ui.getPage().executeJs(
                    "localStorage.removeItem('auth_token');" +
                            "localStorage.removeItem('username');" +
                            "localStorage.removeItem('user_role');"
            );
            ui.getPage().setLocation(LOGOUT_SUCCESS_URL);
        }
    }
}
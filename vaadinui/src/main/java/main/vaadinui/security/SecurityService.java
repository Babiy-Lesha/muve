package main.vaadinui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.RequiredArgsConstructor;
import main.vaadinui.dto.AuthResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";

    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }

    private AuthResponse currentUser;

    public void setCurrentUser(AuthResponse user) {
        this.currentUser = user;
    }

    public AuthResponse getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public Long getCurrentUserId() {
        // В рамках упрощения, считаем, что id пользователя равен хэшкоду его имени пользователя
        // В реальном приложении должно быть получено из токена или с сервера
        return currentUser != null ? (long)currentUser.getUsername().hashCode() : null;
    }
}
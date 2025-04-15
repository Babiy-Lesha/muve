package main.vaadinui.security;

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

    @Getter
    private AuthResponse currentUser;

    @Getter
    @Setter
    private Long userId;

    public void setCurrentUser(AuthResponse user) {
        this.currentUser = user;

        if (user != null) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
            this.userId = user.getId();
        } else {
            SecurityContextHolder.clearContext();
            this.userId = null;
        }
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public Long getCurrentUserId() {
        return userId;
    }
}
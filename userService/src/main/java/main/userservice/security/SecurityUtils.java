package main.userservice.security;

import main.userservice.repository.UserRepository;
import main.userservice.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();
        Long currentUserId = getUserIdFromUsername(currentUsername);

        return userId.equals(currentUserId);
    }

    private Long getUserIdFromUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(0L);
    }
}
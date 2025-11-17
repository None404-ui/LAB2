package api.security;

import entities.Function;
import entities.RoleName;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repositories.UserRepository;

@Service
public class CurrentUserService {

    private static final Logger logger = LoggerFactory.getLogger(CurrentUserService.class);
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        return authentication.getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public boolean hasRole(RoleName roleName) {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName.name()));
    }

    public void requireRole(RoleName roleName) {
        if (!hasRole(roleName)) {
            logger.warn("Access denied: user {} lacks role {}", getCurrentUsername(), roleName);
            throw new AccessDeniedException("Access denied: missing role " + roleName.name());
        }
    }

    public void requireSelfOrAdmin(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Target userId must be provided");
        }
        if (hasRole(RoleName.ROLE_ADMIN)) {
            return;
        }
        Integer currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            logger.warn("Access denied: user {} tried to access user {}", currentUserId, userId);
            throw new AccessDeniedException("Access denied: insufficient privileges for target user");
        }
    }

    public void requireFunctionOwnerOrAdmin(Function function) {
        if (function == null || function.getUser() == null) {
            throw new IllegalArgumentException("Function must contain owner information");
        }
        if (hasRole(RoleName.ROLE_ADMIN)) {
            return;
        }
        Integer currentUserId = getCurrentUserId();
        if (!currentUserId.equals(function.getUser().getUserId())) {
            logger.warn("Access denied: user {} tried to access function {} owned by {}",
                    currentUserId, function.getFunctionId(), function.getUser().getUserId());
            throw new AccessDeniedException("Access denied: insufficient privileges for function");
        }
    }

    public boolean isAdmin() {
        return hasRole(RoleName.ROLE_ADMIN);
    }
}


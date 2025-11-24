package api.service;

import api.dto.CreateUserRequest;
import api.dto.RegisterRequest;
import api.dto.UpdateUserRolesRequest;
import api.dto.UserDto;
import entities.Role;
import entities.RoleName;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.RoleRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        logger.info("Fetching list of all users");
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(CreateUserRequest request) {
        logger.info("Creating user: username={}, email={}", request.getUsername(), request.getEmail());
        validateCreateRequest(request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(resolveRoles(request.getRoles()));

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id={}", savedUser.getUserId());
        return convertToDto(savedUser);
    }

    public UserDto registerUser(RegisterRequest request) {
        logger.info("Registering new user: username={}, email={}", request.getUsername(), request.getEmail());
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // По умолчанию назначаем роль USER
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with id={}", savedUser.getUserId());
        return convertToDto(savedUser);
    }

    public UserDto updateUserRoles(Integer userId, UpdateUserRolesRequest request) {
        Set<String> roles = request == null ? null : request.getRoles();
        logger.info("Updating roles for userId={} with roles={}", userId, roles);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRoles(resolveRoles(roles));
        User savedUser = userRepository.save(user);
        logger.info("Roles updated for userId={}", userId);
        return convertToDto(savedUser);
    }

    private void validateCreateRequest(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<String> rolesInput = requestedRoles == null || requestedRoles.isEmpty()
                ? Set.of(RoleName.ROLE_USER.name())
                : requestedRoles;

        Set<Role> roles = new HashSet<>();
        for (String roleValue : rolesInput) {
            RoleName roleName = normalizeRoleName(roleValue);
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName.name()));
            roles.add(role);
        }
        return roles;
    }

    private RoleName normalizeRoleName(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            throw new IllegalArgumentException("Role value must not be empty");
        }
        String normalized = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        return RoleName.valueOf(normalized);
    }

    private UserDto convertToDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toSet());
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                LocalDateTime.now(),
                roleNames
        );
    }
}

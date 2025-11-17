package api.security;

import entities.Role;
import entities.RoleName;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import repositories.RoleRepository;
import repositories.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class SecurityDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SecurityDataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.default-admin.username:admin}")
    private String defaultAdminUsername;

    @Value("${app.security.default-admin.password:admin123}")
    private String defaultAdminPassword;

    @Value("${app.security.default-admin.email:admin@example.com}")
    private String defaultAdminEmail;

    public SecurityDataInitializer(RoleRepository roleRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ensureRoles();
        ensureDefaultAdmin();
    }

    private void ensureRoles() {
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role(roleName, "Auto-generated role " + roleName.name());
                roleRepository.save(role);
                logger.info("Created missing role {}", roleName.name());
            }
        });
    }

    private void ensureDefaultAdmin() {
        if (userRepository.existsByUsername(defaultAdminUsername)) {
            return;
        }

        logger.warn("Default admin user not found. Creating {} ...", defaultAdminUsername);
        User admin = new User();
        admin.setUsername(defaultAdminUsername);
        admin.setEmail(defaultAdminEmail);
        admin.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Admin role not present"));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("User role not present"));
        roles.add(adminRole);
        roles.add(userRole);
        admin.setRoles(roles);

        userRepository.save(admin);
        logger.info("Default admin user {} created successfully", defaultAdminUsername);
    }
}


package com.magentatechno.pelican.config;

import com.magentatechno.pelican.entity.Role;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer un admin par défaut s'il n'existe pas
        if (!userRepository.existsByEmail("admin@pelican.sn")) {
            User admin = User.builder()
                    .nom("Super")
                    .prenom("Admin")
                    .email("admin@pelican.sn")
                    .password(passwordEncoder.encode("Admin123!"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .accountNonLocked(true)
                    .failedLoginAttempts(0)
                    .service("Direction")
                    .build();
            
            userRepository.save(admin);
            log.info("✅ Admin créé: admin@pelican.sn");
        } else {
            log.info("ℹ️ Admin existe déjà");
        }

        // Créer un utilisateur agent par défaut
        if (!userRepository.existsByEmail("agent@pelican.sn")) {
            User agent = User.builder()
                    .nom("Jean")
                    .prenom("Dupont")
                    .email("agent@pelican.sn")
                    .password(passwordEncoder.encode("Agent123!"))
                    .role(Role.AGENT)
                    .enabled(true)
                    .accountNonLocked(true)
                    .failedLoginAttempts(0)
                    .service("Secrétariat")
                    .build();
            
            userRepository.save(agent);
            log.info("✅ Agent créé: agent@pelican.sn");
        }
    }
}

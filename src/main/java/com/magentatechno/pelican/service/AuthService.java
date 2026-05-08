package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.AuthDto;
import com.magentatechno.pelican.entity.Role;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.exception.BusinessException;
import com.magentatechno.pelican.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Value("${app.security.max-login-attempts}")
    private int maxAttempts;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        log.info("Tentative d'enregistrement pour: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .service(request.getService())
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);
        auditService.log(user.getEmail(), "REGISTER", "USER", true, "Nouvel utilisateur créé");
        log.info("Utilisateur enregistré: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        log.info("Tentative de connexion pour: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    auditService.log(request.getEmail(), "LOGIN_FAILED", "AUTH", false, "Utilisateur non trouvé");
                    return new BusinessException("Identifiants incorrects");
                });

        // Vérifier si le compte est verrouillé
        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null &&
                user.getLockTime().plusMinutes(lockDurationMinutes).isAfter(LocalDateTime.now())) {
                log.warn("Compte verrouillé pour: {}", request.getEmail());
                throw new LockedException("Compte verrouillé. Réessayez après 30 minutes.");
            } else {
                // Déverrouillage automatique
                user.setAccountNonLocked(true);
                user.setFailedLoginAttempts(0);
            }
        }

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BadCredentialsException("Identifiants incorrects");
        }

        // Réinitialiser les tentatives échouées
        user.setFailedLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        auditService.log(user.getEmail(), "LOGIN", "AUTH", true, "Connexion réussie");
        log.info("Connexion réussie pour: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxAttempts) {
            user.setAccountNonLocked(false);
            user.setLockTime(LocalDateTime.now());
            auditService.log(user.getEmail(), "ACCOUNT_LOCKED", "AUTH", false,
                "Compte verrouillé après " + attempts + " tentatives");
            log.warn("Compte verrouillé pour: {} (tentatives: {})", user.getEmail(), attempts);
        }
        userRepository.save(user);
    }

    private AuthDto.AuthResponse generateAuthResponse(User user) {
        return AuthDto.AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .email(user.getEmail())
                .role(user.getRole().name())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .userId(user.getId())
                .build();
    }
}

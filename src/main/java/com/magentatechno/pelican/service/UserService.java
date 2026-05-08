package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.UserDto;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.exception.BusinessException;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public Page<UserDto.Response> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public UserDto.Response findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return mapToResponse(user);
    }

    public UserDto.Response getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return mapToResponse(user);
    }

    @Transactional
    public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setService(request.getService());

        User updated = userRepository.save(user);
        auditService.log(user.getEmail(), "UPDATE_PROFILE", "USER", true, "Profil mis à jour");
        return mapToResponse(updated);
    }

    @Transactional
    public void changePassword(Long id, UserDto.ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Ancien mot de passe incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Les mots de passe ne correspondent pas");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        auditService.log(user.getEmail(), "CHANGE_PASSWORD", "USER", true, "Mot de passe changé");
        log.info("Mot de passe changé pour: {}", user.getEmail());
    }

    @Transactional
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setEnabled(false);
        userRepository.save(user);
        auditService.log(user.getEmail(), "DISABLE_USER", "USER", true, "Utilisateur désactivé");
    }

    @Transactional
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setEnabled(true);
        userRepository.save(user);
        auditService.log(user.getEmail(), "ENABLE_USER", "USER", true, "Utilisateur activé");
    }

    private UserDto.Response mapToResponse(User user) {
        return UserDto.Response.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole().name())
                .service(user.getService())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

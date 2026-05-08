package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.UserDto;
import com.magentatechno.pelican.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto.Response>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto.Response> update(@PathVariable Long id, @Valid @RequestBody UserDto.UpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserDto.ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }
}

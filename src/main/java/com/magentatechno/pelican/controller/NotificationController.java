package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.NotificationDto;
import com.magentatechno.pelican.service.NotificationService;
import com.magentatechno.pelican.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<NotificationDto.Response>> findByUserId(Pageable pageable) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(notificationService.findByUserId(userId, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationDto.Response>> findUnread(Pageable pageable) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(notificationService.findUnreadByUserId(userId, pageable));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread() {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(notificationService.countUnreadByUserId(userId));
    }

    @PostMapping("/{id}/mark-as-read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}

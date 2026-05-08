package com.magentatechno.pelican.service;

import com.magentatechno.pelican.entity.AuditLog;
import com.magentatechno.pelican.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String userEmail, String action, String resource, boolean success, String details) {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();

            AuditLog log = AuditLog.builder()
                    .userEmail(userEmail)
                    .action(action)
                    .resource(resource)
                    .success(success)
                    .details(details)
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            log.warn("Audit logging failed: {}", e.getMessage());
        }
    }
}

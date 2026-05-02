package com.example.hhflow.security;

import com.example.hhflow.model.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static CustomUserDetails currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static CustomUserDetails requireApplicant() {
        CustomUserDetails p = requirePrincipal();
        if (p.getRole() != Role.APPLICANT || p.getApplicantId() == null) {
            throw new AccessDeniedException("Applicant role required");
        }
        return p;
    }

    public static CustomUserDetails requireEmployer() {
        CustomUserDetails p = requirePrincipal();
        if (p.getRole() != Role.EMPLOYER || p.getEmployerId() == null) {
            throw new AccessDeniedException("Employer role required");
        }
        return p;
    }

    private static CustomUserDetails requirePrincipal() {
        CustomUserDetails p = currentPrincipal();
        if (p == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return p;
    }
}

package com.coagronet.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.coagronet.user.User;

@Component
public class AuthenticatedUser {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }
}

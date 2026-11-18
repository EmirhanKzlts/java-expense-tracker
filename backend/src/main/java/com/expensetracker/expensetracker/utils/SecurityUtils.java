package com.expensetracker.expensetracker.utils;

import com.expensetracker.expensetracker.model.User;
import com.expensetracker.expensetracker.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class SecurityUtils {

    public SecurityUtils() {
    }

    public static User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication ==  null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("No authenticated user found");
        }

        return userDetails.getUser();
    }
}

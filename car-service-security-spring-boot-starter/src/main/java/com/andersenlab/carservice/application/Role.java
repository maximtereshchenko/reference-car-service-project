package com.andersenlab.carservice.application;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

enum Role {
    ;

    static final String HUMAN_RESOURCES_SPECIALIST = "HUMAN_RESOURCES_SPECIALIST";
    static final String SALES_SPECIALIST = "SALES_SPECIALIST";
    static final String OPERATIONAL_MANAGER = "OPERATIONAL_MANAGER";
    static final String REGULAR_EMPLOYEE = "REGULAR_EMPLOYEE";
    private static final Set<String> ALL = Set.of(
            HUMAN_RESOURCES_SPECIALIST,
            SALES_SPECIALIST,
            OPERATIONAL_MANAGER,
            REGULAR_EMPLOYEE
    );

    static GrantedAuthority grantedAuthority(String role) {
        if (!ALL.contains(role)) {
            throw new IllegalArgumentException("Unknown role " + role);
        }
        return new SimpleGrantedAuthority("ROLE_" + role);
    }
}

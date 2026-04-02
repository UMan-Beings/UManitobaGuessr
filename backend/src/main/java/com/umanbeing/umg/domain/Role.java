package com.umanbeing.umg.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enumeration representing user roles with Spring Security integration.
 * It provides the following roles:
 * <li>ADMIN - has access to all features</li>
 * <li>USER - has access to most features</li>
 * <li>GUEST - has limited access</li>
 */
public enum Role implements GrantedAuthority {

    /**
     * ADMIN role that has access to all features.
     */
    ADMIN,
    /**
     * USER role that has access to most features.
     */
    USER,
    /**
     * GUEST role that has limited access.
     */
    GUEST;

    @Override
    public String getAuthority() {
        return name();
    }
}

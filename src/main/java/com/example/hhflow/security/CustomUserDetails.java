package com.example.hhflow.security;

import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Пользователь для Spring Security: из БД при логине или из JWT без загрузки сущности.
 * Идентичность — {@link #getUserId()}; роль — {@link Role}.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Long userId;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    private CustomUserDetails(User user, Long userId, String username, String passwordHash, Role role) {
        this.user = user;
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role.authority()));
    }

    public static CustomUserDetails fromUser(User account) {
        return new CustomUserDetails(
                account,
                account.getId(),
                account.getEmail(),
                account.getPasswordHash(),
                account.getRole()
        );
    }

    public static CustomUserDetails fromJwt(Long userId, String username, Role role) {
        String name = username != null && !username.isEmpty() ? username : ("id:" + userId);
        return new CustomUserDetails(null, userId, name, null, role);
    }

    /** Сущность из БД; для JWT без повторной загрузки — {@code null}. */
    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

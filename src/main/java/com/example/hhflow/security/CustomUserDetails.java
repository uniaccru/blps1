package com.example.hhflow.security;

import com.example.hhflow.model.Role;
import com.example.hhflow.model.UserAccount;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Пользователь для Spring Security: из БД при логине или из JWT без загрузки сущности.
 */
public class CustomUserDetails implements UserDetails {

    private final UserAccount userAccount;
    private final Long accountId;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final Long employerId;
    private final Long applicantId;
    private final Collection<? extends GrantedAuthority> authorities;

    private CustomUserDetails(UserAccount userAccount, Long accountId, String username, String passwordHash,
            Role role, Long employerId, Long applicantId) {
        this.userAccount = userAccount;
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.employerId = employerId;
        this.applicantId = applicantId;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role.authority()));
    }

    public static CustomUserDetails fromUserAccount(UserAccount account) {
        Long eid = account.getEmployer() != null ? account.getEmployer().getId() : null;
        Long aid = account.getApplicant() != null ? account.getApplicant().getId() : null;
        return new CustomUserDetails(
                account,
                account.getId(),
                account.getPhone(),
                account.getPasswordHash(),
                account.getRole(),
                eid,
                aid
        );
    }

    public static CustomUserDetails fromJwt(Long accountId, String username, Role role, Long employerId, Long applicantId) {
        String name = username != null && !username.isEmpty() ? username : ("id:" + accountId);
        return new CustomUserDetails(null, accountId, name, null, role, employerId, applicantId);
    }

    /** Сущность из БД; для JWT-пользователя без повторной загрузки — {@code null}. */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Role getRole() {
        return role;
    }

    public Long getEmployerId() {
        return employerId;
    }

    public Long getApplicantId() {
        return applicantId;
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

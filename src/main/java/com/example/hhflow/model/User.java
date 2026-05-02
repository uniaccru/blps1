package com.example.hhflow.model;

import com.example.hhflow.validation.ValidationConstraints;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_accounts")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = ValidationConstraints.PHONE_MAX_LENGTH)
    private String phone;

    @Column(nullable = false, length = ValidationConstraints.PASSWORD_HASH_MAX_LENGTH)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = ValidationConstraints.ROLE_MAX_LENGTH)
    private Role role;

    /** Email — основной способ аутентификации для всех пользователей (employers и applicants). */
    @Column(nullable = false, unique = true, length = ValidationConstraints.EMAIL_MAX_LENGTH)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

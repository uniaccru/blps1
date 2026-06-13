package com.example.hhflow.model;

import com.example.hhflow.validation.ValidationConstraints;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vacancies")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = ValidationConstraints.TITLE_MAX_LENGTH)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;

    @Column(nullable = false)
    private boolean requiresTest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VacancyStatus getStatus() {
        return status;
    }

    public void setStatus(VacancyStatus status) {
        this.status = status;
    }

    public boolean isRequiresTest() {
        return requiresTest;
    }

    public void setRequiresTest(boolean requiresTest) {
        this.requiresTest = requiresTest;
    }

    public User getEmployer() {
        return employer;
    }

    public void setEmployer(User employer) {
        this.employer = employer;
    }
}

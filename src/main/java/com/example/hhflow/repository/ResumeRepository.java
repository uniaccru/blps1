package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.Resume;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByApplicantId(Long applicantId);
}

package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.Resume;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @EntityGraph(attributePaths = "applicant")
    Page<Resume> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "applicant")
    Optional<Resume> findById(Long id);

    @EntityGraph(attributePaths = "applicant")
    Optional<Resume> findByApplicantId(Long applicantId);
}

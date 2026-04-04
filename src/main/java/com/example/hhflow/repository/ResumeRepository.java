package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.Resume;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @EntityGraph(attributePaths = "applicant")
    List<Resume> findAll();

    @EntityGraph(attributePaths = "applicant")
    Optional<Resume> findById(Long id);

    @EntityGraph(attributePaths = "applicant")
    Optional<Resume> findByApplicantId(Long applicantId);
}

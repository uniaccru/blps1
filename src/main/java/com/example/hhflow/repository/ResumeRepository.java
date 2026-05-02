package com.example.hhflow.repository;

import com.example.hhflow.model.Resume;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByApplicantId(Long applicantId);

    Page<Resume> findByApplicant_Id(Long applicantId, Pageable pageable);
}

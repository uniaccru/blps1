package com.example.hhflow.repository;

import com.example.hhflow.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByVacancy_Employer_Id(Long employerId, Pageable pageable);
}

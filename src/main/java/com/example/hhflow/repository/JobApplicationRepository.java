package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.JobApplication;

import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
	@EntityGraph(attributePaths = {"applicant", "vacancy", "resume"})
	Page<JobApplication> findAll(Pageable pageable);

	@EntityGraph(attributePaths = {"applicant", "vacancy", "resume"})
	Optional<JobApplication> findById(Long id);
}

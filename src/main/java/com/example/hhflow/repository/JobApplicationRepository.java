package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.JobApplication;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
	@EntityGraph(attributePaths = {"applicant", "vacancy", "resume"})
	List<JobApplication> findAll();

	@EntityGraph(attributePaths = {"applicant", "vacancy", "resume"})
	Optional<JobApplication> findById(Long id);
}

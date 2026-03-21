package com.example.hhflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hhflow.model.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
